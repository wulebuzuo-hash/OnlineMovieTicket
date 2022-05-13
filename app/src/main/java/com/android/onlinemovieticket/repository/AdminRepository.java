package com.android.onlinemovieticket.repository;

import com.android.onlinemovieticket.db.Admin;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.User;
import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminRepository {
    /**
     * 登录
     */
    public int login(String account, String password, int cid) {
        Connection connection = JDBCUtils.getConn();
        String sql = "select apassword,cid from admin where aaccount = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        int msg = 0;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, account);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                String realpassword = resultSet.getString(1);
                int realcid = resultSet.getInt(2);
                if (password.equals(realpassword)) {
                    msg = 4;    //密码正确,但影院码错误
                    if (realcid == cid) {
                        msg = 1;    //密码正确，影院码正确
                    }
                } else {
                    msg = 2;    //密码错误
                }
            } else msg = 3;//账号不存在
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
     * 查找所有管理员
     */
    public List<Admin> findAllAdmin() {
        List<Admin> adminList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from admin";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Admin admin = new Admin(resultSet.getInt(1),//aid
                        resultSet.getString(2), //aaccount
                        resultSet.getString(3), //apassword
                        resultSet.getInt(4)    //cid
                );
                adminList.add(admin);
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
        return adminList;
    }

    /**
     * 根据账号、影院ID查询管理员账号
     *
     * @param account
     * @param cid
     * @return
     */
    public Admin findAdmin(String account, int cid) {

        Connection connection = JDBCUtils.getConn();
        String sql = "select * from admin where aaccount = ? and cid = ?";
        Admin admin = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, account);
            ps.setInt(2, cid);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                admin = new Admin(resultSet.getInt(1),  //aid
                        resultSet.getString(2),         //aaccount
                        resultSet.getString(3),         //apssword
                        resultSet.getInt(4)            //cid
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

        return admin;
    }

    /**
     * 添加用户信息
     */
    public boolean addAdmin(Admin admin) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into admin(aaccount,apassword,cid) values(?,?,?)";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, admin.getAaccount());
            ps.setString(2, admin.getApassword());
            ps.setInt(3, admin.getCid());
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
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 编辑用户信息
     */
    public boolean updateAdmin(Admin admin) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update admin set apassword = ?,aaccount = ? where cid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, admin.getApassword());
            statement.setString(2, admin.getAaccount());
            statement.setInt(3, admin.getCid());
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

    public boolean delAdmin(int aid) {
        Connection connection = JDBCUtils.getConn();
        String sql = "delete from admin where aid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, aid);
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return true;
            }else return false;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                connection.close();
                statement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }

        }
    }

}
