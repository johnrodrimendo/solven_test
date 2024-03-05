package com.affirm.common.model.form;

import com.affirm.common.util.CharFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question120Form extends FormGeneric implements Serializable {
    private Character frequencyType;
    private Integer monthlyPaymentDay;
    private Integer firstBiweeklyPaymentDay;
    private Integer secondBiweeklyPaymentDay;
    private Integer weeklyPaymentDay;

    public Question120Form() { this.setValidator(new Question120Form.Validator());}

    public class Validator extends FormValidator implements Serializable {
        private CharFieldValidator frequencyType;
        private IntegerFieldValidator monthlyPaymentDay;
        private IntegerFieldValidator firstBiweeklyPaymentDay;
        private IntegerFieldValidator secondBiweeklyPaymentDay;
        private IntegerFieldValidator weeklyPaymentDay;

        public Validator() {
            addValidator(frequencyType = new CharFieldValidator().setRequired(true).setValidValues(new Character[]{'S', 'Q', 'M'}).setFieldName("Frecuencia salarial"));
            addValidator(monthlyPaymentDay = new IntegerFieldValidator().setMaxValue(31).setMinValue(1));
            addValidator(firstBiweeklyPaymentDay = new IntegerFieldValidator().setMaxValue(31).setMinValue(1));
            addValidator(secondBiweeklyPaymentDay = new IntegerFieldValidator().setMaxValue(31).setMinValue(1));
            addValidator(weeklyPaymentDay = new IntegerFieldValidator().setMaxValue(7).setMinValue(1));
        }

        @Override
        protected void setDynamicValidations() {
            if (Question120Form.this.frequencyType != null) {
                switch (Question120Form.this.frequencyType) {
                    case 'S':
                        weeklyPaymentDay.setRequired(true);
                        break;
                    case 'Q':
                        firstBiweeklyPaymentDay.setRequired(true);
                        secondBiweeklyPaymentDay.setRequired(true);
                        break;
                    case 'M':
                        monthlyPaymentDay.setRequired(true);
                        break;
                }
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question120Form.this;
        }
    }

    public Character getFrequencyType() { return frequencyType; }

    public void setFrequencyType(Character frequencyType) { this.frequencyType = frequencyType; }

    public Integer getMonthlyPaymentDay() { return monthlyPaymentDay; }

    public void setMonthlyPaymentDay(Integer monthlyPaymentDay) { this.monthlyPaymentDay = monthlyPaymentDay; }

    public Integer getFirstBiweeklyPaymentDay() { return firstBiweeklyPaymentDay; }

    public void setFirstBiweeklyPaymentDay(Integer firstBiweeklyPaymentDay) {
        this.firstBiweeklyPaymentDay = firstBiweeklyPaymentDay;
    }

    public Integer getSecondBiweeklyPaymentDay() { return secondBiweeklyPaymentDay; }

    public void setSecondBiweeklyPaymentDay(Integer secondBiweeklyPaymentDay) {
        this.secondBiweeklyPaymentDay = secondBiweeklyPaymentDay;
    }

    public Integer getWeeklyPaymentDay() { return weeklyPaymentDay; }

    public void setWeeklyPaymentDay(Integer weeklyPaymentDay) { this.weeklyPaymentDay = weeklyPaymentDay; }
}
