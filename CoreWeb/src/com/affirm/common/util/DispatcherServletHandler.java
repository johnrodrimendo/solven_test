    package com.affirm.common.util;

import com.affirm.common.service.impl.ErrorServiceImpl;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

    /**
 * Created by john on 27/12/16.
 */
public class DispatcherServletHandler extends DispatcherServlet {

    public DispatcherServletHandler(WebApplicationContext context) {
        super(context);
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Date oldDate = new Date();

        try {
            super.doService(request, response);
        } catch (Throwable ex) {
            // Notify error
            ErrorServiceImpl.onErrorStatic(ex);
            // Redirect to error page
            request.getRequestDispatcher("/500").forward(request, response);
        }
        System.out.println("DISPATCHER SERVLE DEMORO: " + (new Date().getTime() - oldDate.getTime()) + "ms [" + request.getRequestURL() + "]");
    }
}