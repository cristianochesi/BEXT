package servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import bext.BextPublisher;
import json.ClientRequest;
import json.DataPoint;
import json.ServerResponse;
import json.SessionData;

/**
 * Servlet implementation class SessionManager
 */
public class SessionManager extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static Logger logger;

  
  
  /**
   * @see HttpServlet#HttpServlet()
   */
  public SessionManager() {
    super();
  }

  
  
  /**
   * @see Servlet#init(ServletConfig)
   */
  public void init(ServletConfig config) throws ServletException {
    logger = LoggerFactory.getLogger(SessionManager.class);
  }

  
  
  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  
  
  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String jsonPars = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    logger.info(jsonPars);
    Gson gson = new Gson();
    ClientRequest cr = gson.fromJson(jsonPars, ClientRequest.class);
    
    ServerResponse sr = null;
    SessionData sd = null;
    String strResponse = null;
    if(cr.getCommand().compareTo("START") == 0) {
      sr = this.startSession(cr);
      strResponse = gson.toJson(sr);
    } else if(cr.getCommand().compareTo("STOP") == 0) {
      sr = this.stopSession(cr);
      strResponse = gson.toJson(sr);
    } else if(cr.getCommand().compareTo("GET") == 0) {
      sd = this.getSessionData(cr); 
      strResponse = gson.toJson(sd);
    }
    
    PrintWriter out = response.getWriter();
    out.println(strResponse);
  }
  
  
  private ServerResponse startSession(ClientRequest cr) {
    Gson gson = new Gson();
    
    ServerResponse sr = new ServerResponse();
    sr.setIdsessione(String.valueOf(System.currentTimeMillis()));
    sr.setTimestamp(System.currentTimeMillis());
    sr.setResult("OK");
    
    ClientRequest deviceCommand = new ClientRequest();
    deviceCommand.setIdsessione(sr.getIdsessione());
    deviceCommand.setTimestamp(sr.getTimestamp());
    deviceCommand.setCommand("START");
    BextPublisher.sendCommand(gson.toJson(deviceCommand));
    return sr;
  }
  
  
  private ServerResponse stopSession(ClientRequest cr) {
    Gson gson = new Gson();
    
    ServerResponse sr = new ServerResponse();
    sr.setIdsessione(cr.getIdsessione());
    sr.setTimestamp(cr.getTimestamp());
    sr.setResult("OK");
    
    ClientRequest deviceCommand = new ClientRequest();
    deviceCommand.setIdsessione(sr.getIdsessione());
    deviceCommand.setTimestamp(sr.getTimestamp());
    deviceCommand.setCommand("STOP");
    BextPublisher.sendCommand(gson.toJson(deviceCommand));
    
    return sr;
  }
  
  
  private SessionData getSessionData(ClientRequest cr) {

    Random r = new Random();
    
    SessionData sd = new SessionData();
    sd.setIdsessione(cr.getIdsessione());
    sd.setTimestamp(cr.getTimestamp());
    
    ArrayList<DataPoint> data = new ArrayList<DataPoint>();
    
    long startTimestamp = Long.parseLong(cr.getIdsessione());
    long stopTimestamp = System.currentTimeMillis();
    long currentTimestamp = startTimestamp;
    
    while (currentTimestamp < stopTimestamp) {
      currentTimestamp += (long)(r.nextInt(3000) + 1000);
      int hr = r.nextInt(62-55) + 55;
      DataPoint dp = new DataPoint(currentTimestamp, hr);
      data.add(dp);
    }
    
    sd.setData(data);
    
    return sd;
  }
  

}
