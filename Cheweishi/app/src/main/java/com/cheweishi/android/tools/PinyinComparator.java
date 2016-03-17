package com.cheweishi.android.tools;

import java.util.Comparator;

import com.cheweishi.android.entity.SortModel;

/**
 * 
 * @author xiaanming
 * 
 */
public class PinyinComparator implements Comparator<SortModel> {

	public int compare(SortModel o1, SortModel o2) {
		System.out.println("拼音" + o1.getSortLetters());
		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
