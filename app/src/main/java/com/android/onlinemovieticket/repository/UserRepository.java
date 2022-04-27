package com.android.onlinemovieticket.repository;

import android.util.Log;

import com.android.onlinemovieticket.db.Admin;
import com.android.onlinemovieticket.db.User;
import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class UserRepository {


    /**
     * 登录
     */
    public int login(String uaccount, String upassword) {
        Connection connection = JDBCUtils.getConn();
        String sql = "select upassword from user where uaccount = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        int msg = 0;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, uaccount);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                String realpassword = resultSet.getString(1);
                if(upassword.equals(realpassword)){
                    msg = 1;    //密码正确
                }else {
                    msg = 2;    //密码错误
                }
            }else msg = 3;//账号不存在
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                ps.close();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }


    /**
     * 查找用户信息
     *
     * @param account
     * @return
     */
    public User findUser(String account) {

        Connection connection = JDBCUtils.getConn();
        String sql = "select * from user where uaccount = ?";
        User user = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, account);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                user = new User(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                ps.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    /**
     * 根据account，查找用户图片
     * @param account
     * @return
     */
    public String getImgByAccount(String account){
        Connection connection = JDBCUtils.getConn();
        String sql = "select uimageString from user where uaccount = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String img = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, account);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                img = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    public int getUserId(String account) {
        Connection connection = JDBCUtils.getConn();
        String sql = "select uid from user where uaccount = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        int uid = 0;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, account);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                uid = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return uid;
    }

    /**
     * 添加用户信息
     */
    public boolean addUser(User user) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into user(uaccount,upassword,uquestion1,uanswer,uquestion2,uanswer2) " +
                "values(?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUaccount());
            ps.setString(2, user.getUpassword());
            ps.setString(3, user.getUquestion1());
            ps.setString(4, user.getUanswer1());
            ps.setString(5, user.getUquestion2());
            ps.setString(6, user.getAnswer2());
            int rs = ps.executeUpdate();
            if (rs > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 编辑用户信息
     */
    public boolean updateUser(User user) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update user set uquestion1 = ?, " +
                "uanswer1 = ?, uquestion2 = ?, uanswer2 = ? where uaccount = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUquestion1());
            statement.setString(2, user.getUanswer1());
            statement.setString(3, user.getUquestion2());
            statement.setString(4, user.getAnswer2());
            statement.setString(5, user.getUaccount());
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改密码
     */
    public boolean updatePass(String account,String password) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update user set upassword = ? where uaccount = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, password);
            statement.setString(2, account);
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            } else return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改头像
     */
    public boolean updateImage(String account,String imageString) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update user set uimageString = ? where uaccount = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, imageString);
            statement.setString(2, account);
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            } else return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查看头像
     */
    public String findImage(String account) {
        Connection connection = JDBCUtils.getConn();
        String sql = "select uimageString from user where uaccount = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String imageString = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,account);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                imageString = resultSet.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imageString;
    }

}
