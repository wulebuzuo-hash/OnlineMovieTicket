package com.android.onlinemovieticket.repository;

import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BossRepository {

    /**
     * 登录
     */
    public int login(String account,String password){
        Connection connection = JDBCUtils.getConn();
        String sql = "select bpassword from boss where baccount = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        int msg = 0;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, account);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                String realpassword = resultSet.getString(1);
                if(password.equals(realpassword)){
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
}
