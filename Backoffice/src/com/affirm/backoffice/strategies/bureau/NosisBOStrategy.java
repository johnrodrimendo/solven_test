package com.affirm.backoffice.strategies.bureau;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.transactional.ApplicationBureau;
import com.affirm.common.util.Marshall;
import com.affirm.nosis.NosisResult;
import org.springframework.ui.ModelMap;

import java.util.Locale;

public class NosisBOStrategy implements BureauStrategy {

    @Override
    public void updateModelMap(Locale locale, ApplicationBureau applicationBureau, ModelMap model, LoanApplicationDAO loanApplicationDAO, PersonDAO personDAO) throws Exception {
        NosisResult nosisResult = applicationBureau.getNosisResult();
        Marshall marshall = new Marshall();
        if (nosisResult != null) {
            if (nosisResult.getParteHTML() != null && nosisResult.getParteHTML().getHtml() != null) {
                model.addAttribute("parteHtml", nosisResult.getParteHTML().getHtml());
                nosisResult.setParteHTML(null);
                System.out.println("datos = " + nosisResult.getParteXML().getDatos().get(0).getNombre());
            }

            model.addAttribute("nosisXml", marshall.toXml(nosisResult));
        }
    }
}
