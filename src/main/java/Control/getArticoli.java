package Control;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Dao.ArticoloDao;
import Connessione.GestoreConnessioneDatabase;
import java.sql.Connection;
/**
 * Servlet implementation class getArticoli
 */
@WebServlet("/getArticoli")
public class getArticoli extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	ArticoloDao articoloDao;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getArticoli() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    public void init() throws ServletException {
		
    	
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		try {
			Connection connection = GestoreConnessioneDatabase.getConnection();
			articoloDao = new ArticoloDao(connection);
			request.setAttribute("articoli", articoloDao.getAllArticoli());
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.getRequestDispatcher("index.jsp").forward(request, response);
		
		
		
		
	}


}
