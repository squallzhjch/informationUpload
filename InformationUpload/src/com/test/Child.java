package com.test;

import java.io.Serializable;

/**
 * Created by zhangjingchao on 15/12/14.
 */
public class Child implements Serializable {

    private String name;
    private int id;
    private int childLeft;
    private int childRight;
    private int peopleNum;
    private int in;
    private int out;

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    public Child(int id, String name, int left, int right, int peopleNum, int in, int out){
        this.name = name;
        this.id = id;
        this.childLeft = left;
        this.childRight = right;
        this.peopleNum = peopleNum;
        this.in = in;
        this.out = out;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChildLeft() {
        return childLeft;
    }

    public void setChildLeft(int childLeft) {
        this.childLeft = childLeft;
    }

    public int getChildRight() {
        return childRight;
    }

    public void setChildRight(int childRight) {
        this.childRight = childRight;
    }
}
