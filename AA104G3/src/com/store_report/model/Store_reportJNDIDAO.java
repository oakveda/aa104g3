package com.store_report.model;

import java.sql.*;
import java.util.*;
import javax.naming.*;
import javax.sql.DataSource;

import com.product_report.model.Product_reportVO;

public class Store_reportJNDIDAO implements Store_reportDAO_interface {
	
	private static DataSource ds = null;
	static{
		try {
			Context ctx = new InitialContext();
			ds = (DataSource)ctx.lookup("java:comp/env/jdbc/TestDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String INSERT_STMT = "INSERT INTO store_report VALUES (lpad(storep_seq.nextval,6,0), ?, ?, ?, ?, 0)";
	private static final String GET_ALL_STMT = "SELECT * FROM store_report where checked = 0 order by storepno";
	private static final String GET_ONE_STMT = "SELECT * FROM store_report where storepno = ?";
	private static final String DELETE = "DELETE FROM store_report where storepno = ?";
	private static final String UPDATE = "UPDATE store_report set checked = ? where storepno = ?";
	private static final String UPDATESAME = "UPDATE store_report set checked = ? where stono = ?";
	private static final String REVOKED = "UPDATE store set stostate = 2 where stono = ?";

	@Override
	public void insert(Store_reportVO storepVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con=ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);			
			pstmt.setString(1, storepVO.getMemno());
			pstmt.setString(2, storepVO.getStono());
			pstmt.setString(3, storepVO.getReason());
			pstmt.setDate(4, storepVO.getRepdate());
			
			pstmt.executeUpdate();

			// Handle any driver errors
		}catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void update(Store_reportVO storepVO) {
		Connection con = null;
		PreparedStatement pstmt = null;		
		try {
			/*對檢舉通過的店家狀態改為撤消，並更改相同店家的檢舉狀態*/
			con = ds.getConnection();
			con.setAutoCommit(false);
			
			pstmt = con.prepareStatement(UPDATE);
			pstmt.setString(1, storepVO.getChecked());
			pstmt.setString(2, storepVO.getStorepno());
			pstmt.executeUpdate();
			
			pstmt = con.prepareStatement(UPDATESAME);
			pstmt.setString(1, storepVO.getChecked());
			pstmt.setString(2, storepVO.getStono());
			pstmt.executeUpdate();
			
			pstmt = con.prepareStatement(REVOKED);
			pstmt.setString(1, storepVO.getStono());			
			pstmt.executeUpdate();
			con.commit();
			
			con.setAutoCommit(true);
			// Handle any driver errors
		}catch (SQLException se) {
			if(con!=null){
				try {
					con.rollback();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void delete(String storepno) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con =ds.getConnection();
			pstmt = con.prepareStatement(DELETE);

			pstmt.setString(1, storepno);

			pstmt.executeUpdate();

			// Handle any driver errors
		}catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public Store_reportVO findByPrimaryKey(String storepno) {
		Store_reportVO storepVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setString(1, storepno);

			rs = pstmt.executeQuery();

			while (rs.next()) {				
				storepVO = new Store_reportVO();
				storepVO.setStorepno(rs.getString("storepno"));
				storepVO.setMemno(rs.getString("memno"));
				storepVO.setStono(rs.getString("stono"));
				storepVO.setReason(rs.getString("reason"));
				storepVO.setRepdate(rs.getDate("repdate"));
				storepVO.setChecked(rs.getString("checked"));
			}

			// Handle any driver errors
		}catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return storepVO;
	}

	@Override
	public List<Store_reportVO> getAll() {
		List<Store_reportVO>  list = new ArrayList<Store_reportVO>();
		Store_reportVO storepVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {				
				storepVO = new Store_reportVO();
				storepVO.setStorepno(rs.getString("storepno"));
				storepVO.setMemno(rs.getString("memno"));
				storepVO.setStono(rs.getString("stono"));
				storepVO.setReason(rs.getString("reason"));
				storepVO.setRepdate(rs.getDate("repdate"));
				storepVO.setChecked(rs.getString("checked"));
				list.add(storepVO);
			}

			// Handle any driver errors
		}catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return list;
	}

}
