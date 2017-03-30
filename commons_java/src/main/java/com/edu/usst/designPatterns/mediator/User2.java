package com.edu.usst.designPatterns.mediator;

/**
 * Created by pengcheng.wan on 2016/8/17.
 */
public class User2 extends User {
    public User2(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void work() {
        System.out.println("user2 exe!");
    }
}
