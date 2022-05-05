package com.android.onlinemovieticket.repository;

import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketRepository {
    /**
     * 添加电影票
     */
    public boolean addTicket(Ticket ticket) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into ticket(ticket_code,uaccount,sid,seat,price,isgrade) " +
                "values(?,?,?,?,?,?)";

        PreparedStatement ps = null;
        try {

            ps = connection.prepareStatement(sql);
            ps.setString(1, ticket.getTicket_code());
            ps.setString(2, ticket.getUaccount());
            ps.setInt(3,ticket.getSid());
            ps.setString(4,ticket.getSeat());
            ps.setDouble(5,ticket.getPrice());
            ps.setInt(6,ticket.getIsGrade());
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
     * 根据uaccount，查询所有电影票
     */
    public List<Ticket> getTicketByAccount(String uaccount){
        List<Ticket> ticketList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from ticket where uaccount = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1,uaccount);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Ticket ticket = new Ticket(resultSet.getInt(1), //tid
                        resultSet.getString(2),         //ticket_code
                        resultSet.getString(3),         //uaccount
                        resultSet.getInt(4),            //sid
                        resultSet.getString(5),        //seat
                        resultSet.getDouble(6),      //price
                        resultSet.getInt(7));          //isGrade
                ticketList.add(ticket);
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
        return ticketList;
    }

    /**
     * 查询所有电影票
     */
    public List<Ticket> getAllTicket(){
        List<Ticket> ticketList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from ticket";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Ticket ticket = new Ticket(resultSet.getInt(1), //tid
                        resultSet.getString(2),         //ticket_code
                        resultSet.getString(3),         //uaccount
                        resultSet.getInt(4),            //sid
                        resultSet.getString(5),        //seat
                        resultSet.getDouble(6),      //price
                        resultSet.getInt(7));          //isGrade
                ticketList.add(ticket);
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
        return ticketList;
    }

    /**
     * 删除电影票
     */
    public boolean deleteTicket(Ticket ticket) {
        Connection connection = JDBCUtils.getConn();
        String sql = "delete from ticket where tid = ?";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, ticket.getTid());
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
     * 根据tid查找,更新isGrade
     */
    public boolean updateIsGrade(int tid,int isGrade){
        Connection connection = JDBCUtils.getConn();
        String sql = "update ticket set isGrade = ? where tid = ?";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1,isGrade);
            ps.setInt(2,tid);
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
}
