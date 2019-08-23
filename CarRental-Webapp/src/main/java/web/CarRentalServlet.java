package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.CarManager;
import project.CustomerManager;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;
import project.impl.Car;
import project.impl.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Servlet for CarRental.
 *
 * @author Daniel Jurca
 */
@WebServlet(web.CarRentalServlet.URL_MAPPING + "/*")
public class CarRentalServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/CarRental";

    private final static Logger log = LoggerFactory.getLogger(web.CarRentalServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET ...");
        showData(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        log.debug("POST ... {}",action);
        switch (action) {
            case "/addCar":
                addCar(request,response);
                break;
            case "/deleteCar":
                deleteCar(request, response);
                break;
            case "/updateCar":
                editCar(request, response);
                return;
            case "/addCustomer":
                addCustomer(request, response);
                break;
            case "/deleteCustomer":
                break;
            case "/updateCustomer":
                break;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private CarManager getCarManager() {
        return (CarManager) getServletContext().getAttribute("carManager");
    }

    private CustomerManager getCustomerManager() {
        return (CustomerManager) getServletContext().getAttribute("customerManager");
    }

    private void showData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.debug("showing table of cars");
            request.setAttribute("cars", getCarManager().findAllCars());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (IllegalEntityException | ValidationException e) {
            log.error("Cannot show cars", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void addCar(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String carBrand = request.getParameter("carBrand");
        String description = request.getParameter("description");

        if (carBrand == null || carBrand.length() == 0 || description == null || description.length() == 0 ||
                request.getParameter("dailyPrice").isEmpty()) {
            request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty !");
            log.debug("form data invalid");
            showData(request, response);
            return;

        }
        BigDecimal dailyPrice = new BigDecimal(request.getParameter("dailyPrice"));
        try {
            Car car = new Car(null, carBrand, description, dailyPrice);
            getCarManager().createCar(car);
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
            return;
        } catch (IllegalEntityException | ValidationException e) {
            log.error("Cannot add car", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    private void deleteCar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        try {
            Long id = Long.valueOf(request.getParameter("carId"));
            getCarManager().deleteCar(getCarManager().getCarById(id));
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
            return;
        } catch (IllegalEntityException | ValidationException e) {
            log.error("Cannot delete car", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    private void editCar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        Long id = Long.valueOf(request.getParameter("carId"));

    }

    private void addCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String phoneNumber = request.getParameter("phoneNumber");

        if (fullName == null || fullName.length() == 0 || address == null || address.length() == 0 ||
                phoneNumber == null || phoneNumber.length() == 0) {
            request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty !");
            log.debug("form data invalid");
            showData(request, response);
            return;

        }
        try {
            Customer customer = new Customer(null, fullName, address, phoneNumber);
            getCustomerManager().createCustomer(customer);
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
            return;
        } catch (IllegalEntityException | ValidationException e) {
            log.error("Cannot add customer", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }
}
