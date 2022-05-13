package com.android.onlinemovieticket.repository;

import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HallRepository {

    /**
     * 根据hid,获取hname
     */
    public String getHnameByHid(int hid) {
        String hname = null;
        Connection connection = JDBCUtils.getConn();
        String sql = "select hname from hall where hid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, hid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                hname = resultSet.getString("hname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return hname;
    }

    /**
     * 根据hid，查找放映厅
     */
    public Hall getHallByHid(int hid) {
        Hall hall = null;
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from hall where hid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, hid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                hall = new Hall(resultSet.getInt(1),//hid
                        resultSet.getInt(2),    //cid
                        resultSet.getString(3), //hname
                        resultSet.getInt(4),  //row
                        resultSet.getInt(5) //column
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return hall;
    }

    /**
     * 显示该影院的所有放映厅
     */
    public List<Hall> findAllHall(int cid) {
        List<Hall> hallList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from hall where cid = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, cid);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Hall hall = new Hall(resultSet.getInt(1),//hid
                        resultSet.getInt(2),    //cid
                        resultSet.getString(3), //hname
                        resultSet.getInt(4),  //row
                        resultSet.getInt(5) //column
                );
                hallList.add(hall);
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
        return hallList;
    }

    /**
     * 查找该电影院下的某个放映厅
     */
    public Hall findHall(String hname, int cid) {
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from hall where hname = ? and cid = ?";
        Hall hall = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, hname);
            ps.setInt(2, cid);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                hall = new Hall(resultSet.getInt(1),//hid
                        resultSet.getInt(2),    //cid
                        resultSet.getString(3), //hname
                        resultSet.getInt(4),  //row
                        resultSet.getInt(5)    //column
                );
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
        return hall;
    }

    /**
     * 更新该电影院下的某个放映厅
     */
    public boolean addHall(Hall hall) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into hall(cid,hname,row,column2) " +
                "values(?,?,?,?)";

        PreparedStatement ps = null;
        try {

            ps = connection.prepareStatement(sql);
            ps.setInt(1, hall.getCid());
            ps.setString(2, hall.getHname());
            ps.setInt(3, hall.getRow());
            ps.setInt(4, hall.getColumn());

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
     * 更新放映厅信息
     */
    public boolean updateHall(Hall hall) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update hall set hname = ?,row = ?,column2 = ? where hid = ? and cid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);

            statement.setString(1, hall.getHname());
            statement.setInt(2, hall.getRow());
            statement.setInt(3, hall.getColumn());
            statement.setInt(4, hall.getHid());
            statement.setInt(5, hall.getCid());
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除放映厅
     */
    public boolean delHall(final Hall hall) {
        Connection connection = JDBCUtils.getConn();
        String sql = "delete from hall where hid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, hall.getHid());
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
