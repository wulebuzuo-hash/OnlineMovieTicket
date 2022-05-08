package com.android.onlinemovieticket.repository;

import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CinemaRepository {

    /**
     * 查找所有电影院
     * @return
     */
    public List<Cinema> findAllCinema(){
        List<Cinema> cinemaList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from cinema";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Cinema cinema = new Cinema(resultSet.getInt(1),//cid
                        resultSet.getString(2), //cname
                        resultSet.getString(3),  //cposition
                        resultSet.getString(4)  //ccall
                );
                cinemaList.add(cinema);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
                ps.close();
                resultSet.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cinemaList;
    }

    /**
     * 根据cid，查找cname
     */
    public String getCnameByCid(int cid){
        Connection connection = JDBCUtils.getConn();
        String sql = "select cname from cinema where cid = ?";
        String cname = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, cid);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                cname = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
                ps.close();
                resultSet.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cname;
    }

    /**
     * 根据cid，查找电影院
     */
    public Cinema getCinemaByCid(int cid){
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from cinema where cid = ?";
        Cinema cinema = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, cid);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                cinema = new Cinema(resultSet.getInt(1),//cid
                        resultSet.getString(2), //cname
                        resultSet.getString(3),  //cposition
                        resultSet.getString(4)  //ccall
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
                ps.close();
                resultSet.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cinema;
    }

    /**
     * 添加电影院信息
     */
    public boolean addCinema(Cinema cinema) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into cinema(cname,cposition,ccall) " +
                "values(?,?,?)";
        PreparedStatement ps = null;
        try {

            ps = connection.prepareStatement(sql);
            ps.setString(1, cinema.getCname());
            ps.setString(2,cinema.getCposition());
            ps.setString(3,cinema.getCcall());
            int rs = ps.executeUpdate();
            if (rs > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                connection.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新电影院信息
     */
    public boolean updateCinema(Cinema cinema){
        Connection connection = JDBCUtils.getConn();
        String sql = "update cinema set cname = ?,cposition = ?,ccall = ? where cid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);

            statement.setString(1,cinema.getCname());
            statement.setString(2, cinema.getCposition());
            statement.setString(3, cinema.getCcall());
            statement.setInt(4, cinema.getCid());
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }  finally {
            try {
                connection.close();
                statement.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除电影院信息
     */
    public boolean delCinema(int cid){
        Connection connection = JDBCUtils.getConn();
        String sql = "delete from cinema where cid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, cid);
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }  finally {
            try {
                connection.close();
                statement.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
