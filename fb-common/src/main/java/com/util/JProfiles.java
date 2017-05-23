package com.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

final public class JProfiles {
	
	private static Map<String, ProfileNode> nodes = new HashMap<String, ProfileNode>(1024);

	private static int maxLength;

	public static final ThreadLocal<ProfileNode> session = new ThreadLocal<ProfileNode>();

	private static ProfileNode getNode(String title) {
		synchronized (nodes) {
			ProfileNode node = nodes.get(title);
			if (node == null) {
				node = new ProfileNode(title);
				node.source = node;
				nodes.put(title, node);
				maxLength = Math.max(maxLength, title.length());
			}
			// 被多线程用了
			ProfileNode orig = node;
			node = new ProfileNode(title);
			node.source = orig;
			// 标记已经启动了
			node.start = 1;
			return node;
		}
	}

	public static void reset() {
		nodes = new HashMap<String, ProfileNode>(100);
	}

	/**
	 * 打印出所有的统计情况
	 */
	synchronized public static String[] dump() {
		Queue<ProfileNode> queue;
		synchronized (nodes) {
			// 马上复制一份
			queue = new PriorityQueue<ProfileNode>(nodes.values().size(),
					new Comparator<ProfileNode>() {

						@Override
						public int compare(ProfileNode o1, ProfileNode o2) {
							if (o1.cnt == 0 || o2.cnt == 0) {
								return (int) (o1.sum
										/ (o1.cnt == 0 ? 1 : o1.cnt) - o2.sum
										/ (o2.cnt == 0 ? 1 : o2.cnt));
							} else {
								return (int) (o1.sum / o1.cnt - o2.sum / o2.cnt);
							}
						}

					});
			queue.addAll(nodes.values());
		}
		String title = String.format("%-1s|\t%8s\t%8s\t%8s\t%8s\t%8s",
				doubleChar("title", maxLength * 2), "cnt", "avg(us)",
				"min(us)", "max(us)", "fst(us)");
		System.out.println(title);
		String[] result = new String[queue.size() + 1];
		int i = 0;
		result[i++] = title;
		while (queue.size() > 0) {
			ProfileNode node = queue.poll();
			if (node.min == Long.MAX_VALUE) {
				result[i++] = null;
				continue;
			}
			String template = "%-1s|\t%8d\t%8d\t%8d\t%8d\t%8d";
			String tmp = String.format(template//
					, doubleChar(node.title, maxLength * 2)//
					, node.cnt//
					, (int) ((node.cnt == 0 ? 0 : node.sum / node.cnt) / 1000)//
					, node.min / 1000//
					, node.max / 1000//
					, node.fst / 1000 //
					);
			result[i++] = tmp;
			System.out.println(tmp);
		}
		return result;
	}

	private static String doubleChar(String title, int length) {
		length = (int) (Math.ceil(length / 8d) * 8);
		int cnt = length - title.length();
		for (char c : title.toCharArray()) {
			if (c < 128 && c > 0) {
				;
			} else {
				cnt--;
			}
		}
		while (cnt > 0) {
			title += " ";
			cnt--;
		}
		return title;
	}

	/**
	 * 手动的一个采样测试
	 * 
	 * @param title
	 * @param start
	 * @param end
	 */
	public static void sampling(String title, long start, long end) {
		ProfileNode info = getNode(title);
		info.source.submit(end - start);
	}

	/**
	 * 手动的一个采样测试
	 * 
	 * @param title
	 * @param start
	 * @param end
	 * @param threshold
	 */
	public static void sampling(String title, long start, long end,
			long threshold) {
		if (end - start < threshold) {
			// 忽略掉太小的
			return;
		}
		ProfileNode info = getNode(title);
		info.source.submit(end - start);
	}

	/**
	 * 启动一个测试
	 * 
	 * @param title
	 */
	public static void start(String title) {
		long s = System.nanoTime();
		ProfileNode old = session.get();
		ProfileNode info = getNode(title);
		if (old != null) {
			info.parent = old;
		}
		session.set(info);
		info.start = System.nanoTime();
		info.end = info.start;
		sampling("ProfileStart", s, info.start);
	}

	/**
	 * 结束了
	 * 
	 * @param message
	 */
	public static void end(String message) {
		long now = System.nanoTime();
		ProfileNode curInfo = session.get();
		ProfileNode info = curInfo;
		if (curInfo == null) {
			System.out.println("一个没有开始的结束被触发了");
			return;
		}
		long duration = now - curInfo.start;
		if (curInfo.start == 0) {
			// 这个不应该的
			System.err.println("错误的Profile数据|" + curInfo.title);
			duration = 0;
		}
		if (curInfo.parent != null) {
			// 针对嵌套特殊处理一下
			session.set(curInfo.parent);
			curInfo.parent = null;
		} else {
			session.remove();
		}
		info = info.source;
		info.duration += duration;
		info.submit();
		sampling("ProfileEnd", now, System.nanoTime());
	}
}

class ProfileNode {
	
	public ProfileNode source;

	public long start;
	
	public long duration;
	
	public long end;

	final public String title;

	/**
	 * 最大执行时间
	 */
	public long max = 0;

	/**
	 * 最小执行时间
	 */
	public long min = Long.MAX_VALUE;

	/**
	 * 第一次执行时间
	 */
	public long fst;

	/**
	 * 总次数
	 */
	public long cnt;

	/**
	 * 总时间
	 */
	public long sum;

	/**
	 * 父节点
	 */
	public ProfileNode parent;

	public ProfileNode(String title) {
		this.title = title;
	}

	public void submit(long duration) {
		sum += duration;
		if (cnt++ == 0) {
			fst = duration;
		}
		if (duration < min) {
			min = duration;
		}
		if (duration > max) {
			max = duration;
		}
	}

	/**
	 * 提交一次数据
	 */
	public void submit() {
		sum += duration;
		if (cnt++ == 0) {
			fst = duration;
		}
		if (duration < min) {
			min = duration;
		}
		if (duration > max) {
			max = duration;
		}
		start = 0;
		end = 0;
		duration = 0;
	}

	/**
	 * 单纯的重置
	 */
	public void reset() {
		start = 0;
		end = 0;
		duration = 0;
	}
}
