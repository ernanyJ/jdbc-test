package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao {

    Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department department) {
        PreparedStatement st = null;

        try {
            conn.setAutoCommit(false);
            st = conn.prepareStatement("""
                    INSERT INTO department (Name)
                    VALUES (?)
                    """, Statement.RETURN_GENERATED_KEYS);

            st.setString(1, department.getName());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) department.setId(rs.getInt(1));
            }
            conn.commit();
        } catch (SQLException e) {
            rollbackTransaction(conn);
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Department department) {
        PreparedStatement st = null;

        try {
            conn.setAutoCommit(false);
            st = conn.prepareStatement("""
                    UPDATE department SET Name = ?
                    WHERE Id = ?
                    """);

            st.setString(1, department.getName());
            st.setInt(2, department.getId());
            st.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            rollbackTransaction(conn);
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;

        try {
            conn.setAutoCommit(false);
            st = conn.prepareStatement("""
                    DELETE FROM DEPARTMENT
                    WHERE id = ?;
                    """);

            st.setInt(1, id);
            st.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            rollbackTransaction(conn);
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("""    
                    SELECT * from department
                    Where id = ?              
                    """);

            st.setInt(1, id);

            rs = st.executeQuery();
            if (rs.next()) {
                Department dep = new Department(rs.getInt("Id"), rs.getString("Name"));
                return dep;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Department> findAll() {
        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("""
                    SELECT * FROM department
                    """);

            rs = st.executeQuery();
            List<Department> list = new ArrayList<>();

            while (rs.next()) {
                Department dep = new Department(rs.getInt("Id"), rs.getString("Name"));
                list.add(dep);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

    }

    private void rollbackTransaction(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

}



