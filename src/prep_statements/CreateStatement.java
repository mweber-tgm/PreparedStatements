/**
 * 
 */
package prep_statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Martin Weber
 * @version 24.02.2015
 *
 */
public class CreateStatement {
	private PreparedStatement create;

	public CreateStatement(DBConnector con) {
		create = con.prepareStatement("INSERT INTO person VALUES(?,?,?)");
	}

	/**
	 * Method to insert a person into the postgresql database  schokofabrik 
	 * into the table person
	 * with Prepared Statements
	 * 
	 * @param number id of the person
	 * @param name the name of the person
	 * @param surname the surname of the person
	 */
	public void insertPerson(int number, String name, String surname) {
		try {
			create.setInt(1, number);
			create.setString(2, name);
			create.setString(3, surname);
			create.execute();
		} catch (SQLException e) {
			System.err.println("Inserting a new Person failed");
			e.printStackTrace();
		}
	}

	/**
	 * Method to create a defined amount of random inserts with the insertPerson Method
	 * The generated inserts are filled with useless data
	 * 
	 * @param count number of inserts 
	 * @param start id where the inserts should start
	 */
	public void createRandomPerson(int count, int start) {
		for (int i = start; i < count + start; i++) {
			/*
			 * Auf Stackoverflow gefunden
			 * generiert random ID 
			 */
			String s= UUID.randomUUID().toString().substring(0, 20);
			insertPerson(i,s,s);
		}
	}
}
