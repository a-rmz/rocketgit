package com.rocketgit.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import com.rocketgit.objects.Repository;

public class DBQueryRepository {
	
	private DataSource ds;
	
	public DBQueryRepository(DataSource ds) {
		this.ds = ds;
	}
	
	public ArrayList<Repository> getRepository() {
		ArrayList<Repository> list = new ArrayList<Repository>();
		try (
				Connection conn = ds.getConnection(); 
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("Select * from rocketgit.repository");
				) {
			while(rs.next()) {
				list.add(new Repository(rs.getString(2), rs.getString(3), rs.getInt(1)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list; 
		
	}
	
	public boolean putRepository(Repository repository) {
		int result = 0;
		try (
				Connection conn = ds.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO rocketgit.repository(Name, Path) values (?, ?)");
				) {
			stmt.setString(1, repository.getName());
			stmt.setString(2, repository.getPath());
			
			result = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(result == 1) {
			System.out.println(result);
			return true;
		}
		else {
			System.out.println(result);
			return false;
		}
	}
	
	
	public boolean deleteRepository(Repository repository) {
		System.out.println(repository.getId());
		int result = 0;
		try (
				Connection conn = ds.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement("DELETE FROM rocketgit.repository WHERE name = ? AND path = ? AND IdRepository = ?");
				) {
			stmt.setString(1, repository.getName());
			stmt.setString(2, repository.getPath());
			stmt.setInt(3, repository.getId());
			
			result = stmt.executeUpdate();
			System.out.println("result delete = " + result);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if(result == 1) return true;
		else return false;
	}
	
	
	public boolean updateNameRepository(Repository repository) {
		int result = 0;
		try (
				Connection conn = ds.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement("UPDATE rocketgit.repository SET name = ? WHERE IdRepository = ?");
				) {
			stmt.setString(1, repository.getName());
			stmt.setInt(2, repository.getId());
			
			result = stmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if(result == 1) return true;
		else return false;
	}
	
	
	public int getRepositoryId(Repository repo) {
		int result = 0;
		try (
				Connection conn = ds.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement("Select IdRepository from rocketgit.repository WHERE name = ? AND path = ?");
				) {
			stmt.setString(1, repo.getName());
			stmt.setString(2, repo.getPath());
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result; 
	}
	
}
