package com.hjlc.util.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象序列化工具
 */

public class ObjectUtil {
	/**对象转byte[]
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] objectToBytes(Object obj) throws Exception{
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        byte[] bytes = bo.toByteArray();
        bo.close();
        oo.close();
        return bytes;
    }
    /**byte[]转对象
     * @param bytes
     * @return
     * @throws Exception
     */
    public static Object bytesToObject(byte[] bytes) throws Exception{
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream sIn = new ObjectInputStream(in);
        return sIn.readObject();
    }
    /**
	 * 将LIST集合转化成二维字节数组
	 * @param pdList  要转化的集合
	 * @return
	 */
	public static byte[][] listToByteArray(List<PageData> pdList) {
		if (pdList == null || pdList.isEmpty()) 
			return null;
		byte[][] byteArray = new byte[pdList.size()][];
		try {
			for(int p = 0; p < pdList.size(); p ++){
				byteArray[p] = objectToBytes(pdList.get(p));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteArray;
	}
	/**
	 * 将二维字节数组转化成LIST集合
	 * @param byteArray  要转化的二维字节数组
	 * @return
	 */
	public static List<PageData> byteArrayToList(byte[][] byteArray) {
		if (byteArray == null || byteArray.length < 1) 
			return null;
		List<PageData> pdList = new ArrayList<PageData>(byteArray.length + 3);
		try {
			for(int p = 0; p < byteArray.length; p ++){
				Object o = bytesToObject(byteArray[p]);
				if(o != null){
					PageData pData = (PageData) o;
					pdList.add(pData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pdList;
	}
	
	/**
	 * 将二维字节数组转化成LIST集合
	 * @param byteArray  要转化的二维字节数组
	 * @return
	 */
	public static List<PageData> byteListToPageDataList(List<byte[]> byteList) {
		if (byteList == null || byteList.isEmpty()) 
			return null;
		List<PageData> pdList = new ArrayList<PageData>(byteList.size() + 3);
		try {
			for(byte[] bs : byteList){
				Object o = bytesToObject(bs);
				if(o != null){
					PageData pData = (PageData) o;
					pdList.add(pData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pdList;
	}
}
