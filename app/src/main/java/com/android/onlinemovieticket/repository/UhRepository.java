package com.android.onlinemovieticket.repository;

import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Uh;
import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UhRepository {

    /**
     * 增加uh
     */
    public boolean addUh(Uh uh) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into uh(uhcontent,uh_updateid,uh_table,account,uh_date) " +
                "values(?,?,?,?,?)";

        PreparedStatement ps = null;
        try {

            ps = connection.prepareStatement(sql);
            ps.setString(1, uh.getUhcontent());
            ps.setInt(2, uh.getUh_updateId());
            ps.setString(3, uh.getUh_table());
            ps.setString(4, uh.getUhaccount());
            ps.setDate(5, new java.sql.Date(uh.getUhDate().getTime()));

            int rs = ps.executeUpdate();
            if (rs > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据account，查找所有uh
     */
    public List<Uh> findAllUhByAccount(String account) {
        List<Uh> uhList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from uh where account = ? order by uh_date desc";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, account);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Uh uh = new Uh(resultSet.getInt(1),//uh_id
                        resultSet.getString(2),    //content
                        resultSet.getInt(3), //uh_updateId
                        resultSet.getString(4),  //table_name
                        resultSet.getString(5),  //account
                        new Date(resultSet.getDate(6).getTime()));//uh_date
                uhList.add(uh);
            }
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
        return uhList;
    }
}
