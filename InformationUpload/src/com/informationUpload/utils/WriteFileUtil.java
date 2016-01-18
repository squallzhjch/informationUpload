package com.informationUpload.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class WriteFileUtil {
	/**
	 * 文档保存的文件路径
	 */
	private static String 	path = Environment
			.getExternalStorageDirectory()+"/InformationUpload/Upload/";
	/**
	 * 将list写到文件中
	 * @param list
	 */
	public static void doWriteFile(ArrayList<String> list){


		if(!new File(path).exists()){
			new File(path).mkdirs();
		}

		FileWriter fw = null;
		PrintWriter pw = null;
		try{
			//创建字符流
			fw = new FileWriter(path+"poi.txt");
			Log.i("chentao","fw:"+path+"poi.txt");
			//封装字符流的过滤流
			pw = new PrintWriter(fw);
			//文件写入
			for(int i=0;i<list.size();i++){
				pw.print(list.get(i)+"\r\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			//关闭外层流
			if(pw != null){
				pw.close();
			}
		}
	}
}
