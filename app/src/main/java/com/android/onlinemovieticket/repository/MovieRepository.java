package com.android.onlinemovieticket.repository;

import android.database.Cursor;
import android.util.Base64;

import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.User;
import com.android.onlinemovieticket.utils.JDBCUtils;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    /**
     * 蜘蛛侠：英雄归来
     *
     * 在《美国队长3：内战》的莱比锡机场大战结束后，彼得·帕克在导师托尼·斯塔克的协助下，试图在一名普通高中生和打击犯罪的超级英雄蜘蛛侠间保持平衡，但反派秃鹫的崛起让人们面临新的威胁。钢铁侠作为蜘蛛侠的导师再次出现，作为引导他踏上英雄之路的关键人物，确保他在未来有资格成为复仇者联盟的一分子。
     *
     * 战狼2
     * 被开除军籍的冷锋本是因找杀害龙小云的凶手来到非洲，但是却突然被卷入一场非洲国家的叛乱。因为国家之间政治立场的关系，中国军队无法在非洲实行武装行动撤离华侨。而作为退伍老兵的冷锋无法忘记曾经为军人的使命，本来可以安全撤离的他毅然决然地回到了沦陷区，孤身一人带领身陷屠杀中的同胞和难民，展开生死逃亡。随着斗争的持续，体内的狼性逐渐复苏，最终闯入战乱区域，为同胞而战斗
     */

    /**
     * 查找所有正在上映的电影
     */
    public List<Movie> findAllMovie() {
        List<Movie> movieList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from movie order by mpf desc";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Movie movie = new Movie(resultSet.getInt(1),//mid
                        resultSet.getString(2), //mname
                        resultSet.getString(3), //mname_eng
                        resultSet.getString(4), //mscreen
                        resultSet.getString(5), //mtype
                        resultSet.getString(6), //mstory
                        resultSet.getInt(7),    //mlong
                        resultSet.getString(8), //mimg
                        resultSet.getDate(9),   //showdate
                        resultSet.getDate(10),   //downdate
                        resultSet.getString(11), //mactor
                        resultSet.getString(12), //mdir
                        resultSet.getDouble(13),    //mpf
                        resultSet.getInt(14),   //mscall
                        resultSet.getInt(15)    //mscnum
                );
                movieList.add(movie);
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
        return movieList;
    }

    /**
     * 根据mid，查找电影
     */
    public Movie getMovieByMid(int mid) {
        Movie movie = null;
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from movie where mid = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, mid);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                movie = new Movie(resultSet.getInt(1),//mid
                        resultSet.getString(2), //mname
                        resultSet.getString(3), //mname_eng
                        resultSet.getString(4), //mscreen
                        resultSet.getString(5), //mtype
                        resultSet.getString(6), //mstory
                        resultSet.getInt(7),    //mlong
                        resultSet.getString(8), //mimg
                        resultSet.getDate(9),   //showdate
                        resultSet.getDate(10),   //downdate
                        resultSet.getString(11), //mactor
                        resultSet.getString(12), //mdir
                        resultSet.getDouble(13),    //mpf
                        resultSet.getInt(14),   //mscall
                        resultSet.getInt(15)    //mscnum
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
        return movie;
    }

    /**
     * 根据mid，查找电影
     */
    public double getMpfByMid(int mid) {
        double mpf = 0;
        Connection connection = JDBCUtils.getConn();
        String sql = "select mpf from movie where mid = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, mid);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                mpf = resultSet.getDouble(1);
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
        return mpf;
    }

    /**
     * 查找某部电影
     *
     * @param mname
     * @return
     */

    public Movie findMovie(String mname) {
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from movie where mname = ?";
        Movie movie = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, mname);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                movie = new Movie(resultSet.getInt(1),//mid
                        resultSet.getString(2), //mname
                        resultSet.getString(3), //mname_eng
                        resultSet.getString(4), //mscreen
                        resultSet.getString(5), //mtype
                        resultSet.getString(6), //mstory
                        resultSet.getInt(7),    //mlong
                        resultSet.getString(8), //mimg
                        resultSet.getDate(9),   //showdate
                        resultSet.getDate(10),   //downdate
                        resultSet.getString(11), //mactor
                        resultSet.getString(12), //mdir
                        resultSet.getDouble(13),    //mpf
                        resultSet.getInt(14),   //mscall
                        resultSet.getInt(15)    //mscnum
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
        return movie;
    }


    /**
     * 添加电影信息
     */
    public boolean addMovie(Movie movie) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into movie(mname,mname_eng,mscreen,mtype,mstory,mlong,img," +
                "showdate,downdate,mactor,mdir,mpf,mscall,mscnum) " +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try {

            ps = connection.prepareStatement(sql);
            ps.setString(1, movie.getMname());
            ps.setString(2, movie.getMname_eng());
            ps.setString(3, movie.getMscreen());
            ps.setString(4, movie.getMtype());
            ps.setString(5, movie.getMstory());
            ps.setInt(6, movie.getMlong());
            ps.setString(7, movie.getImgString());
            ps.setDate(8, movie.getShowdate());
            ps.setDate(9, movie.getDowndate());
            ps.setString(10, movie.getMactor());
            ps.setString(11, movie.getMdir());
            ps.setDouble(12, 0);
            ps.setInt(13, 10);
            ps.setInt(14, 1);
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
     * 更新电影信息
     *
     * @param movie
     * @return
     */
    public boolean updateMovie(Movie movie) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update movie set mname = ?,mname_eng = ?,mscreen = ?,mtype = ?,mstory = ?,mlong = ?," +
                "img = ?,showdate = ?,downdate = ?,mactor = ?,mdir = ? where mid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);

            statement.setString(1, movie.getMname());
            statement.setString(2, movie.getMname_eng());
            statement.setString(3, movie.getMscreen());
            statement.setString(4, movie.getMtype());
            statement.setString(5, movie.getMstory());
            statement.setInt(6, movie.getMlong());
            statement.setString(7, movie.getImgString());
            statement.setDate(8, movie.getShowdate());
            statement.setDate(9, movie.getDowndate());
            statement.setString(10, movie.getMactor());
            statement.setString(11, movie.getMdir());
            statement.setInt(12, movie.getMid());
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
     * 删除电影信息
     * @param mid
     * @return
     */
    public boolean deleteMovie(int mid) {
        Connection connection = JDBCUtils.getConn();
        String sql = "delete from movie where mid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, mid);
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
     * 更新电影票房
     */
    public boolean updatePf(double mpf, int mid) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update movie set mpf = ? where mid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setDouble(1, mpf);
            statement.setInt(2, mid);
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
     * 更新电影评分
     */
    public boolean updateMsc(int mscall, int mscnum, int mid) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update movie set mscall = ?,mscnum = ? where mid = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, mscall);
            statement.setInt(2, mscnum);
            statement.setInt(3, mid);
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
