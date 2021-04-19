package com.anuvg.preazyadmin;

class UserDetailCardView {
    private String userEndId, userName;

    UserDetailCardView(String userEndId, String userName) {
        this.userEndId = userEndId;
        this.userName = userName;
    }

    String getUserEndId() {
        return userEndId;
    }

    String getUserName() {
        return userName;
    }
}