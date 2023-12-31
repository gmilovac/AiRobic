package edu.brown.cs.student.main.handlers;

import edu.brown.cs.student.main.database.DatabaseCommands;
import edu.brown.cs.student.main.server.serializing.Serializer;
import java.io.IOException;
import java.util.HashMap;
import spark.Request;
import spark.Response;
import spark.Route;

/** This class deletes the user from the database */
public class DeleteUser implements Route {

  public DeleteUser() throws IOException, InterruptedException {}

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String username = request.queryParams("username");
    HashMap<String, Object> output = new HashMap<>();
    if (username == null) {
      output.put("result", "error_bad_request");
      output.put("message", "ERROR: Invalid input.");
    } else {
      new DatabaseCommands().delete("users/" + username);
      output.put("result", "success");
      output.put("message", "Successfully deleted " + username);
    }
    return Serializer.serialize(output);
  }
}
