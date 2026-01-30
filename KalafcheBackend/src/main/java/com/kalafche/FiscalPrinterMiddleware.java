package com.kalafche;
//
//import com.fazecast.jSerialComm.SerialPort;
//import com.sun.net.httpserver.HttpServer;
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.InputStreamReader;
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.net.InetSocketAddress;
//
public class FiscalPrinterMiddleware {
//
//	private static final int PORT = 9000;
//	private static final String COM_PORT = "COM3"; // Change as needed
//
//	public static void main(String[] args) throws IOException {
//		HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
//		server.createContext("/print", new PrintHandler());
//		server.setExecutor(null);
//		System.out.println("Middleware listening on http://localhost:" + PORT);
//		server.start();
//	}
//
//	static class PrintHandler implements HttpHandler {
//		@Override
//		public void handle(HttpExchange exchange) throws IOException {
//			if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
//				InputStream is = exchange.getRequestBody();
//				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//				StringBuilder requestBody = new StringBuilder();
//				String line;
//				while ((line = reader.readLine()) != null) {
//					requestBody.append(line);
//				}
//				reader.close();
//
//				System.out.println("Received print request: " + requestBody);
//
//				boolean success = sendToFiscalPrinter("Hello from Datecs middleware!\n");
//				String response = success ? "Printed successfully" : "Failed to print";
//				exchange.sendResponseHeaders(success ? 200 : 500, response.length());
//				OutputStream os = exchange.getResponseBody();
//				os.write(response.getBytes());
//				os.close();
//			} else {
//				exchange.sendResponseHeaders(405, -1); // Method Not Allowed
//			}
//		}
//	}
//
//	private static boolean sendToFiscalPrinter(String data) {
//		SerialPort comPort = SerialPort.getCommPort(COM_PORT);
//		comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
//		comPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
//
//		if (!comPort.openPort()) {
//			System.err.println("Failed to open COM port: " + COM_PORT);
//			return false;
//		}
//
//		try {
//			byte[] bytes = data.getBytes();
//			comPort.getOutputStream().write(bytes);
//			comPort.getOutputStream().flush();
//			System.out.println("Data sent to printer.");
//			return true;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			comPort.closePort();
//		}
//	}
}
