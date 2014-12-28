package com.tyan.hmm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class HmmModel {
	Hashtable wordfre = new Hashtable(); // wordfrequency，存储单个词的词性及其频率
	Hashtable word2_pa_fre = new Hashtable();; // hash2，存储每两个词的词性及其频率
	Hashtable word_pa_fre = new Hashtable();; // hash3,存储词语、词性和词频
	String[] part_table; // table_pos[]用于存储所有不同的词性符号
	int trainwc; // 统计词性个数
	double[][] status; // status[i][j]用于存储转移概率,表示由状态j转移到状态i的概率。
	String[] part1; // Part1[]数组用于存储单个词的词性标注符号 除去part[0] = null

	public void train(String filepath) {
		// 统计出训练样本中词性种类及其频率
		String content = "";
		BufferedReader reader = null;
		try { // 读取文本中的内容，并保存在content的字符流中
			reader = new BufferedReader(new FileReader(filepath));
			String line;
			while ((line = reader.readLine()) != null)
				content += line;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		String[] trainword; // trainword[]用于存储训练样本中的词语
		trainword = content.split("(/[a-z]*\\s{0,})|(][a-z]*\\s{1,})"); // 去除词性标注
		String[] part; // Part[]数组用于存储单个词的词性标注符号
		part = content.split("[0-9|-]*/|\\s{1,}[^a-z]*|][a-z]"); // 仅保留词性标注符号。
		part1 = new String[part.length - 1];// 去除temp[0]为空的情况
		for (int i = 0; i < part.length - 1; i++)
			part1[i] = part[i + 1];

		String[] part_w2; // temp2[]数组用于存储每两个词的词性标注符号
		part_w2 = new String[part1.length - 1];
		for (int i = 0; i < part1.length - 1; i++)
			part_w2[i] = part1[i] + ',' + part1[i + 1];

		String[] w_part;
		w_part = new String[trainword.length];
		for (int i = 0; i < trainword.length; i++)
			w_part[i] = trainword[i] + ',' + part1[i];

		for (String wd : part1) {
			if (wordfre.containsKey(wd))
				wordfre.put(wd, wordfre.get(wd).hashCode() + 1);
			else
				wordfre.put(wd, 1);
		}
		trainwc = wordfre.size(); // 统计词性个数

		for (String wd : part_w2) {
			if (word2_pa_fre.containsKey(wd))
				word2_pa_fre.put(wd, word2_pa_fre.get(wd).hashCode() + 1);
			else
				word2_pa_fre.put(wd, 1);
		}

		for (String wd : w_part) {
			if (word_pa_fre.containsKey(wd))
				word_pa_fre.put(wd, word_pa_fre.get(wd).hashCode() + 1);
			else
				word_pa_fre.put(wd, 1);
		}

		part_table = new String[trainwc];
		Enumeration key = wordfre.keys();
		for (int i = 0; i < trainwc; i++) {
			String str = (String) key.nextElement();
			part_table[i] = str;
		}

		// status[i][j]用于存储转移概率,表示由状态j转移到状态i的概率。
		status = new double[trainwc][trainwc];
		for (int i = 0; i < trainwc; i++) // 初始化
		{
			for (int j = 0; j < trainwc; j++)
				status[i][j] = 0;
		}

		for (int i = 0; i < trainwc; i++) {
			for (int j = 0; j < trainwc; j++) {
				String wd = part_table[j];
				String str = wd + ',' + part_table[i];
				if (word2_pa_fre.containsKey(str))
					status[i][j] = Math
							.log(((double) word2_pa_fre.get(str).hashCode() / (double) wordfre
									.get(wd).hashCode()) * 100000000);
				else
					status[i][j] = Math.log((1 / ((double) wordfre.get(wd)
							.hashCode() * 1000)) * 100000000);
			}
		}

	}

	public String[] posTag(String[] input) {
		// 计算发射概率
		int inputwc = input.length;
		double[][] observe; // observe[i][j]表示在词性状态Sj下，输出词语Oi的概率。
		observe = new double[inputwc][trainwc];
		for (int i = 0; i < inputwc; i++) // 初始化
		{
			for (int j = 0; j < trainwc; j++)
				observe[i][j] = 0;
		}

		for (int i = 0; i < inputwc; i++) {
			for (int j = 0; j < trainwc; j++) {
				String wd = input[i];
				String ws = part_table[j];
				String str = wd + ',' + ws;
				if (word_pa_fre.containsKey(str))
					observe[i][j] = Math
							.log(((double) word_pa_fre.get(str).hashCode() / (double) wordfre
									.get(ws).hashCode()) * 100000000);
				else
					observe[i][j] = Math.log((1 / ((double) wordfre.get(ws)
							.hashCode() * 1000)) * 100000000);
			}
		}
		
		// Viterbi算法，进行词性标注。找出the best path
		double[][] wordmax; // path[][]存储单个词语的最大概率
		wordmax = new double[inputwc][trainwc];
		for (int i = 0; i < inputwc; i++) {
			for (int j = 0; j < trainwc; j++)
				wordmax[i][j] = 0.0;
		}

		int[][] backpointer; // backpointer[][]记录单个词中每个词性取得最大概率时所对应的前一个词性的位置
		backpointer = new int[inputwc][trainwc];
		for (int i = 0; i < inputwc; i++) {
			for (int j = 0; j < trainwc; j++)
				backpointer[i][j] = 0;
		}

		for (int s = 0; s < trainwc; s++) // 对test[]中的第一个词，初始化在每个词性下产生该词的概率。
		{
			wordmax[0][s] = Math.log(((double) wordfre.get(part_table[s])
					.hashCode() / (double) part1.length) * 100000000)
					+ observe[0][s];
		}

		for (int i = 1; i < inputwc; i++) // 对test[]中剩下的词，依次计算单个词性对应的最大概率并记录其位置
		{
			for (int j = 0; j < trainwc; j++) {
				double maxp = wordmax[i - 1][0] + status[j][0] + observe[i][j];
				int index = 0;
				for (int k = 1; k < trainwc; k++) {
					wordmax[i][j] = wordmax[i - 1][k] + status[j][k] + observe[i][j];
					if (wordmax[i][j] > maxp) {
						index = k;
						maxp = wordmax[i][j];
					}
				}
				backpointer[i][j] = index;
				wordmax[i][j] = maxp;
			}
		}

		// 回溯遍历，找出概率最大的路径,输出结果
		int maxindex = 0; // 记录测试文本中最后一个词取得最大概率的位置。
		double max = wordmax[inputwc - 1][0];
		for (int i = 1; i < trainwc; i++) {
			if (wordmax[inputwc - 1][i] > max) {
				maxindex = i;
				max = wordmax[inputwc - 1][maxindex];
			}
		}

		String[] result; // 存储词性标注结果
		String[] object; // 存储结果集中的所有词性，用于计算精确度
		result = new String[inputwc];
		object = new String[inputwc];
		result[inputwc - 1] = input[inputwc - 1] + '/' + part_table[maxindex];
		object[inputwc - 1] = part_table[maxindex];
		int t = 0;
		int front = maxindex;
		for (int i = inputwc - 2; i >= 0; i--) {
			t = backpointer[i + 1][front];
			result[i] = input[i] + '/' + part_table[t] + "  ";
			object[i] = part_table[t];
			front = t;
		}

		return result;
	}

	public static void main(String[] args) {
		HmmModel test1 = new HmmModel();
		test1.train("199801train.txt");
		String[] test = { "会议", "强调", "要", "把", "提高", "统计", "数据", "质量", "摆",
				"到", "更加", "突出", "的", "战略", "地位", "来", "抓" };
		String result[] = test1.posTag(test);
		   for (int i = 0; i < result.length; i++)
			      System.out.println(result[i] + "");

	}
}
