package com.android.onlinemovieticket.repository;

import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessionRepository {

    /**
     * 查找该影院，该电影的所有场次
     */
    public List<Session> findAllSession(int cid, int mid) {
        List<Session> sessionList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from session where cid = ? and mid = ? order by showdate,showtime";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, cid);
            ps.setInt(2, mid);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Session session = new Session(resultSet.getInt(1),//sid
                        resultSet.getInt(2),    //cid
                        resultSet.getInt(3),    //hid
                        resultSet.getInt(4),  //mid
                        new Date(resultSet.getDate(5).getTime()),  //showdate
                        new Date(resultSet.getTime(6).getTime()),  //showtime
                        new Date(resultSet.getTime(7).getTime()),  //downtime
                        resultSet.getDouble(8), //price
                        resultSet.getString(9)  //state
                );
                sessionList.add(session);
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
        return sessionList;
    }

    /**
     * 根据sid，查找场次
     */
    public Session getSessionBySid(int sid) {
        Session session = null;
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from session where sid = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, sid);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                session = new Session(resultSet.getInt(1),//sid
                        resultSet.getInt(2),    //cid
                        resultSet.getInt(3),    //hid
                        resultSet.getInt(4),  //mid
                        new Date(resultSet.getDate(5).getTime()),  //showdate
                        new Date(resultSet.getTime(6).getTime()),  //showtime
                        new Date(resultSet.getTime(7).getTime()),  //downtime
                        resultSet.getDouble(8), //price
                        resultSet.getString(9)  //state
                );
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
        return session;
    }

    /**
     *
     */
    public int getSessionId(Session session) {
        int sid = 0;
        Connection connection = JDBCUtils.getConn();
        String sql = "select sid from session where" +
                " cid = ? and mid = ? and showdate = ? and showtime = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, session.getCid());
            ps.setInt(2, session.getMid());
            ps.setDate(3, new java.sql.Date(session.getShowDate().getTime()));
            ps.setDate(4, new java.sql.Date(session.getShowTime().getTime()));
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                sid = resultSet.getInt(1);
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
        return sid;
    }

    /**
     * 根据cid，查找场次
     */
    public List<Session> getSessionByCid(int cid) {
        List<Session> sessionList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from session where cid = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, cid);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Session session = new Session(resultSet.getInt(1),//sid
                        resultSet.getInt(2),    //cid
                        resultSet.getInt(3),    //hid
                        resultSet.getInt(4),  //mid
                        new Date(resultSet.getDate(5).getTime()),  //showdate
                        new Date(resultSet.getTime(6).getTime()),  //showtime
                        new Date(resultSet.getTime(7).getTime()),  //downtime
                        resultSet.getDouble(8), //price
                        resultSet.getString(9)  //state
                );
                sessionList.add(session);
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
        return sessionList;
    }

    /**
     * 根据mid，查找场次
     */
    public List<Session> getSessionByMid(int mid) {
        List<Session> sessionList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from session where mid = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, mid);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Session session = new Session(resultSet.getInt(1),//sid
                        resultSet.getInt(2),    //cid
                        resultSet.getInt(3),    //hid
                        resultSet.getInt(4),  //mid
                        new Date(resultSet.getDate(5).getTime()),  //showdate
                        new Date(resultSet.getTime(6).getTime()),  //showtime
                        new Date(resultSet.getTime(7).getTime()),  //downtime
                        resultSet.getDouble(8), //price
                        resultSet.getString(9)  //state
                );
                sessionList.add(session);
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
        return sessionList;
    }

    /**
     * 添加放映场次
     */
    public boolean addSession(Session session) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into session(cid,hid,mid,showdate,showtime,endtime,price,state) " +
                "values(?,?,?,?,?,?,?,?)";

        PreparedStatement ps = null;
        try {

            ps = connection.prepareStatement(sql);
            ps.setInt(1, session.getCid());
            ps.setInt(2,session.getHid());
            ps.setInt(3, session.getMid());
            ps.setDate(4, new java.sql.Date(session.getShowDate().getTime()));
            ps.setTime(5, new java.sql.Time(session.getShowTime().getTime()));
            ps.setTime(6, new java.sql.Time(session.getEndTime().getTime()));
            ps.setDouble(7, session.getPrice());
            ps.setString(8, session.getState());

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
     * 更新场次座位情况
     * @param sid
     * @param state
     * @return
     */
    public boolean updateState(int sid,String state) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update session set state = ? where sid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, state);
            statement.setInt(2, sid);
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
     * 更新场次信息
     */
    public boolean updateSession(Session session) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update session set cid = ?,hid = ?,mid = ?," +
                "showdate = ?,showtime = ?,endtime = ?,price = ?,state = ? where sid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, session.getCid());
            statement.setInt(2, session.getHid());
            statement.setInt(3, session.getMid());
            statement.setDate(4, new java.sql.Date(session.getShowDate().getTime()));
            statement.setTime(5, new java.sql.Time(session.getShowTime().getTime()));
            statement.setTime(6, new java.sql.Time(session.getEndTime().getTime()));
            statement.setDouble(7, session.getPrice());
            statement.setString(8, session.getState());
            statement.setInt(9, session.getSid());
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            } else return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
