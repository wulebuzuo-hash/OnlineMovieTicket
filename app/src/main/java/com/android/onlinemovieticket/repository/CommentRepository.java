package com.android.onlinemovieticket.repository;

import com.android.onlinemovieticket.db.Comment;
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

public class CommentRepository {

    /**
     * 根据影片id获取评论
     */
    public List<Comment> getCommentByMid(int mid) {
        List<Comment> commentList = new ArrayList<>();
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from comment where mid = ? order by good_num desc";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, mid);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Comment comment = new Comment(resultSet.getInt(1),  // comment_id
                        resultSet.getInt(2),  // mid
                        resultSet.getString(3),  // user_image
                        resultSet.getString(4),  // uaccount
                        resultSet.getInt(5),  // sc
                        resultSet.getString(6),  // comment_text
                        new Date(resultSet.getDate(7).getTime()),  // comment_time
                        resultSet.getInt(8),  // good_num
                        resultSet.getString(9),  // good_user_id
                        resultSet.getInt(10)  // other_comment_id
                );
                commentList.add(comment);
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
        return commentList;
    }

    public Comment getCommentBycommentId(int comment_id) {
        Comment comment = null;
        Connection connection = JDBCUtils.getConn();
        String sql = "select * from comment where comment_id = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, comment_id);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                comment = new Comment(resultSet.getInt(1),  // comment_id
                        resultSet.getInt(2),  // mid
                        resultSet.getString(3),  // user_image
                        resultSet.getString(4),  // uaccount
                        resultSet.getInt(5),  // sc
                        resultSet.getString(6),  // comment_text
                        new Date(resultSet.getDate(7).getTime()),  // comment_time
                        resultSet.getInt(8),  // good_num
                        resultSet.getString(9),  // good_user_id
                        resultSet.getInt(10)  // other_comment_id
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
        return comment;
    }

    /**
     * 添加评论
     */
    public int addComment(Comment comment) {
        Connection connection = JDBCUtils.getConn();
        String sql = "insert into comment(mid, user_image, uaccount, sc, comment_text, " +
                "comment_time, good_num, good_account, other_comment_id) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        int result = 0;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, comment.getMid());
            ps.setString(2, comment.getUser_image());
            ps.setString(3, comment.getUaccount());
            ps.setInt(4, comment.getSc());
            ps.setString(5, comment.getComment_text());
            ps.setDate(6, new java.sql.Date(comment.getComment_time().getTime()));
            ps.setInt(7, comment.getGood_num());
            ps.setString(8, comment.getGood_user_id());
            ps.setInt(9, comment.getOther_comment_id());
            result = ps.executeUpdate();
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
        return result;
    }

    /**
     * 更新点赞人数
     */
    public int updateGoodNum(int comment_id, int good_num,String good_account) {
        Connection connection = JDBCUtils.getConn();
        String sql = "update comment set good_num = ?,good_account = ? where comment_id = ?";
        PreparedStatement ps = null;
        int result = 0;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, good_num);
            ps.setString(2, good_account);
            ps.setInt(3, comment_id);
            result = ps.executeUpdate();
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
        return result;
    }

    /**
     * 删除评论
     */
    public int deleteComment(int comment_id) {
        Connection connection = JDBCUtils.getConn();
        String sql = "delete from comment where comment_id = ?";
        PreparedStatement ps = null;
        int result = 0;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, comment_id);
            result = ps.executeUpdate();
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
        return result;
    }

}
