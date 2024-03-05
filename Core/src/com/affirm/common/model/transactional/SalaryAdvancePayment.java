package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Employer;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 24/08/16.
 */
public class SalaryAdvancePayment {

    private Employer employer;
    private EmployerCreditStats stats;
    private List<MultiCreditPayment> cashSurplus;
    private Integer[] creditIds;
    private List<MultiCreditPayment> payments;
    private List<Credit> credits;
    private List<PersonCreditsSalaryAdvancePayment> personCredits;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        if (JsonUtil.getIntFromJson(json, "employer_id", null) != null) {
            setEmployer(catalog.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        }
        if (JsonUtil.getJsonObjectFromJson(json, "employer_stats", null) != null) {
            stats = new EmployerCreditStats();
            stats.fillFromDb(JsonUtil.getJsonObjectFromJson(json, "employer_stats", null));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "cash_surplus", null) != null) {
            cashSurplus = new ArrayList<>();
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "cash_surplus", null);
            for (int i = 0; i < array.length(); i++) {
                MultiCreditPayment payment = new MultiCreditPayment();
                payment.fillFromDb(array.getJSONObject(i), catalog, locale);
                cashSurplus.add(payment);
            }
        }
        if (JsonUtil.getJsonArrayFromJson(json, "credit_id", null) != null) {
            List<Integer> creditIdsList = JsonUtil.getListFromJsonArray(json.getJSONArray("credit_id"), (arr, i) -> arr.getInt(i));
            setCreditIds(creditIdsList.toArray(new Integer[creditIdsList.size()]));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "payments", null) != null) {
            payments = new ArrayList<>();
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "payments", null);
            for (int i = 0; i < array.length(); i++) {
                MultiCreditPayment payment = new MultiCreditPayment();
                payment.fillFromDb(array.getJSONObject(i), catalog, locale);
                payments.add(payment);
            }
        }
    }

    public Double getTotalPaymentAmount() {
        if (payments == null)
            return 0.0;
        MutableDouble total = new MutableDouble();
        payments.stream().forEach(p -> total.add(p.getAmount()));
        return total.doubleValue();
    }

    public Double getTotalSurplusAmount() {
        if (cashSurplus == null)
            return 0.0;
        MutableDouble total = new MutableDouble();
        cashSurplus.stream().forEach(p -> total.add(p.getCashSurplus()));
        return total.doubleValue();
    }

    public void fillPersonCredits() {
        if (credits == null)
            return;

        HashMap<Integer, List<Credit>> hashMap = new HashMap<>();
        credits.stream().forEach(c -> {
            if (!hashMap.containsKey(c.getPersonId())) {
                List<Credit> list = new ArrayList<>();
                list.add(c);

                hashMap.put(c.getPersonId(), list);
            } else {
                hashMap.get(c.getPersonId()).add(c);
            }
        });

        setPersonCredits(new ArrayList<>());
        hashMap.forEach((k, v) -> {
            PersonCreditsSalaryAdvancePayment p = new PersonCreditsSalaryAdvancePayment();
            p.setPersonId(k);
            p.setDocType(v.get(0).getPersonDocumentType());
            p.setDocNumber(v.get(0).getPersonDocumentNumber());
            p.setFullName(v.get(0).getFullName());
            p.setCredits(v);
            getPersonCredits().add(p);
        });

    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public EmployerCreditStats getStats() {
        return stats;
    }

    public void setStats(EmployerCreditStats stats) {
        this.stats = stats;
    }

    public List<MultiCreditPayment> getCashSurplus() {
        return cashSurplus;
    }

    public void setCashSurplus(List<MultiCreditPayment> cashSurplus) {
        this.cashSurplus = cashSurplus;
    }

    public Integer[] getCreditIds() {
        return creditIds;
    }

    public void setCreditIds(Integer[] creditIds) {
        this.creditIds = creditIds;
    }

    public List<MultiCreditPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<MultiCreditPayment> payments) {
        this.payments = payments;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
        fillPersonCredits();
    }

    public List<PersonCreditsSalaryAdvancePayment> getPersonCredits() {
        return personCredits;
    }

    public void setPersonCredits(List<PersonCreditsSalaryAdvancePayment> personCredits) {
        this.personCredits = personCredits;
    }
}
