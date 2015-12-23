package com.test;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjingchao on 15/8/20.
 */
public class test {

    private static List<int[]> mList = new ArrayList<int[]>();
    private static Config mConfig = new Config();
    public static void main(String[] args) {

        int left = 0;
        int right = 0;
        int zong = 0;
        int zz = 0;
//        for(int i = 1; i < 37; i++){
//            Child child = mConfig.getChild(i);
//            zong += child.getChildLeft() + child.getChildRight();
//            System.out.println(child.getId() + " " + child.getName() + " " + mConfig.getChild(child.getChildLeft()).getName() + ", " + mConfig.getChild(child.getChildRight()).getName());
//            zz += i;
//        }
//        System.out.println(zong + " " + zz*2);

        for(int i = 1; i < 2; i++){
            Child child = mConfig.getChild(i);
            int[] array = new int[9];
            array[0] = child.getId();
            mList.add(array);
            getChilds(array, child, 9, 1);
            for(int j = 0; j<mList.size();j++){
                int[] a = mList.get(j);
//                if(a[8] != 0) {
                    StringBuffer buffer = new StringBuffer();
                    int in = 0;
                    int out = 0;
                    int peo = 0;
                    for (int it : a) {
                        Child child1 = mConfig.getChild(it);
                        buffer.append(child1.getName());
                        buffer.append("-");
                        in += child1.getIn();
                        out += child1.getOut();
                        peo += child1.getPeopleNum();
                    }

                    buffer.append("out=");
                    buffer.append(out);
                    buffer.append("in=");
                    buffer.append(in);
                    buffer.append(" - ");
                    buffer.append(out - in);
                    buffer.append(" peo =");
                    buffer.append(peo);
                    System.out.println(buffer.toString());
//                }
            }
        }
    }
    private static void getChilds(int[] array, Child child, int zongCeng, int index){
        if(zongCeng == index){
            return;
        }


        Child childLeft = mConfig.getChild(child.getChildLeft());
        Child childRight = mConfig.getChild(child.getChildRight());


        int[] arrayRight = new int[zongCeng];

        for(int i = 0; i< index; i++){
            arrayRight[i] = array[i];
        }

        array[index] = childLeft.getId();
        arrayRight[index] = childRight.getId();
        mList.add(arrayRight);
        index ++;

        boolean his = false;
        for(int i = 0; i < index -1; i++){
            if(array[i] == childLeft.getId()){
                his = true;
                break;
            }
        }

        if(!his){
            getChilds(array, childLeft, zongCeng, index);
        }else{
            mList.remove(array);
        }


        his = false;
        for(int i = 0; i < index - 1; i++){
            if(arrayRight[i] == childRight.getId()){
                his = true;
                break;
            }
        }

        if(!his){
            getChilds(arrayRight, childRight, zongCeng, index);
        }else{
            mList.remove(arrayRight);
        }
    }
}
