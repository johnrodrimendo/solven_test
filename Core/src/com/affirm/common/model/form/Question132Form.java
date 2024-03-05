package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

public class Question132Form extends FormGeneric implements Serializable {

    private Question26Form question26Form;
    private Question27Form question27Form;
    private Question88Form question88Form;
    private AddressForm question82Form;
    private Question28Form question28Form;
    private Question29Form question29Form;
    private Question30Form question30Form;
    private Question31Form question31Form;
    private Question131Form question131Form;
    private Question32Form question32Form;
    private Question38Form question38Form;
    private Question90Form question90Form;
    private AddressForm question61Form;
    private Question37Form question46Form;
    private Question33Form question33Form;
    private Question89Form question89Form;
    private Question34Form question34Form;
    private Question37Form question37Form;
    private Question92Form question92Form;
    private Question91Form question91Form;
    private Question47Form question47Form;
    private Question98Form question98Form;
    private Question120Form question120Form;
    private Question40Form question40Form;
    private Question41Form question41Form;
    private Question42Form question42Form;
    private Question43Form question43Form;
    private Question44Form question44Form;
    private Question45Form question45Form;
    private Question35Form question35Form;
    private Question36Form question36Form;
    private Question104Form question104Form;
    private Question39Form question39Form;
    private Question99Form question99Form;
    private AddressForm question100Form;
    private Question101Form question101Form;
    private Question103Form question103Form;

    public void parseFromJson(JSONArray formJsons) {
        for (int i = 0; i < formJsons.length(); i++) {
            JSONObject formJson = formJsons.getJSONObject(i);
            if (formJson != null && formJson.has("data"))
                switch (formJson.getInt("id")) {
                    case 26:
                        setQuestion26Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question26Form.class));
                        break;
                    case 27:
                        setQuestion27Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question27Form.class));
                        break;
                    case 88:
                        setQuestion88Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question88Form.class));
                        break;
                    case 82:
                        setQuestion82Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), AddressForm.class));
                        break;
                    case 28:
                        setQuestion28Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question28Form.class));
                        break;
                    case 29:
                        setQuestion29Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question29Form.class));
                        break;
                    case 30:
                        setQuestion30Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question30Form.class));
                        break;
                    case 31:
                        setQuestion31Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question31Form.class));
                        break;
                    case 131:
                        setQuestion131Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question131Form.class));
                        break;
                    case 32:
                        setQuestion32Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question32Form.class));
                        break;
                    case 38:
                        setQuestion38Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question38Form.class));
                        break;
                    case 90:
                        setQuestion90Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question90Form.class));
                        break;
                    case 61:
                        setQuestion61Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), AddressForm.class));
                        break;
                    case 46:
                        setQuestion46Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question37Form.class));
                        break;
                    case 33:
                        setQuestion33Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question33Form.class));
                        break;
                    case 89:
                        setQuestion89Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question89Form.class));
                        break;
                    case 34:
                        setQuestion34Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question34Form.class));
                        break;
                    case 37:
                        setQuestion37Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question37Form.class));
                        break;
                    case 92:
                        setQuestion92Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question92Form.class));
                        break;
                    case 91:
                        setQuestion91Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question91Form.class));
                        break;
                    case 47:
                        setQuestion47Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question47Form.class));
                        break;
                    case 98:
                        setQuestion98Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question98Form.class));
                        break;
                    case 120:
                        setQuestion120Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question120Form.class));
                        break;
                    case 40:
                        setQuestion40Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question40Form.class));
                        break;
                    case 41:
                        setQuestion41Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question41Form.class));
                        break;
                    case 42:
                        setQuestion42Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question42Form.class));
                        break;
                    case 43:
                        setQuestion43Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question43Form.class));
                        break;
                    case 44:
                        setQuestion44Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question44Form.class));
                        break;
                    case 45:
                        setQuestion45Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question45Form.class));
                        break;
                    case 35:
                        setQuestion35Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question35Form.class));
                        break;
                    case 36:
                        setQuestion36Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question36Form.class));
                        break;
                    case 104:
                        setQuestion104Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question104Form.class));
                        break;
                    case 39:
                        setQuestion39Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question39Form.class));
                        break;
                    case 99:
                        setQuestion99Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question99Form.class));
                        break;
                    case 100:
                        setQuestion100Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), AddressForm.class));
                        break;
                    case 101:
                        setQuestion101Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question101Form.class));
                        break;
                    case 103:
                        setQuestion103Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question103Form.class));
                        break;
                }
        }
    }

    public Question26Form getQuestion26Form() {
        return question26Form;
    }

    public void setQuestion26Form(Question26Form question26Form) {
        this.question26Form = question26Form;
    }

    public Question27Form getQuestion27Form() {
        return question27Form;
    }

    public void setQuestion27Form(Question27Form question27Form) {
        this.question27Form = question27Form;
    }

    public Question88Form getQuestion88Form() {
        return question88Form;
    }

    public void setQuestion88Form(Question88Form question88Form) {
        this.question88Form = question88Form;
    }

    public AddressForm getQuestion82Form() {
        return question82Form;
    }

    public void setQuestion82Form(AddressForm question82Form) {
        this.question82Form = question82Form;
    }

    public Question28Form getQuestion28Form() {
        return question28Form;
    }

    public void setQuestion28Form(Question28Form question28Form) {
        this.question28Form = question28Form;
    }

    public Question29Form getQuestion29Form() {
        return question29Form;
    }

    public void setQuestion29Form(Question29Form question29Form) {
        this.question29Form = question29Form;
    }

    public Question30Form getQuestion30Form() {
        return question30Form;
    }

    public void setQuestion30Form(Question30Form question30Form) {
        this.question30Form = question30Form;
    }

    public Question31Form getQuestion31Form() {
        return question31Form;
    }

    public void setQuestion31Form(Question31Form question31Form) {
        this.question31Form = question31Form;
    }

    public Question131Form getQuestion131Form() {
        return question131Form;
    }

    public void setQuestion131Form(Question131Form question131Form) {
        this.question131Form = question131Form;
    }

    public Question32Form getQuestion32Form() {
        return question32Form;
    }

    public void setQuestion32Form(Question32Form question32Form) {
        this.question32Form = question32Form;
    }

    public Question38Form getQuestion38Form() {
        return question38Form;
    }

    public void setQuestion38Form(Question38Form question38Form) {
        this.question38Form = question38Form;
    }

    public Question90Form getQuestion90Form() {
        return question90Form;
    }

    public void setQuestion90Form(Question90Form question90Form) {
        this.question90Form = question90Form;
    }

    public AddressForm getQuestion61Form() {
        return question61Form;
    }

    public void setQuestion61Form(AddressForm question61Form) {
        this.question61Form = question61Form;
    }

    public Question37Form getQuestion46Form() {
        return question46Form;
    }

    public void setQuestion46Form(Question37Form question46Form) {
        this.question46Form = question46Form;
    }

    public Question33Form getQuestion33Form() {
        return question33Form;
    }

    public void setQuestion33Form(Question33Form question33Form) {
        this.question33Form = question33Form;
    }

    public Question89Form getQuestion89Form() {
        return question89Form;
    }

    public void setQuestion89Form(Question89Form question89Form) {
        this.question89Form = question89Form;
    }

    public Question34Form getQuestion34Form() {
        return question34Form;
    }

    public void setQuestion34Form(Question34Form question34Form) {
        this.question34Form = question34Form;
    }

    public Question37Form getQuestion37Form() {
        return question37Form;
    }

    public void setQuestion37Form(Question37Form question37Form) {
        this.question37Form = question37Form;
    }

    public Question92Form getQuestion92Form() {
        return question92Form;
    }

    public void setQuestion92Form(Question92Form question92Form) {
        this.question92Form = question92Form;
    }

    public Question91Form getQuestion91Form() {
        return question91Form;
    }

    public void setQuestion91Form(Question91Form question91Form) {
        this.question91Form = question91Form;
    }

    public Question47Form getQuestion47Form() {
        return question47Form;
    }

    public void setQuestion47Form(Question47Form question47Form) {
        this.question47Form = question47Form;
    }

    public Question98Form getQuestion98Form() {
        return question98Form;
    }

    public void setQuestion98Form(Question98Form question98Form) {
        this.question98Form = question98Form;
    }

    public Question120Form getQuestion120Form() {
        return question120Form;
    }

    public void setQuestion120Form(Question120Form question120Form) {
        this.question120Form = question120Form;
    }

    public Question40Form getQuestion40Form() {
        return question40Form;
    }

    public void setQuestion40Form(Question40Form question40Form) {
        this.question40Form = question40Form;
    }

    public Question41Form getQuestion41Form() {
        return question41Form;
    }

    public void setQuestion41Form(Question41Form question41Form) {
        this.question41Form = question41Form;
    }

    public Question42Form getQuestion42Form() {
        return question42Form;
    }

    public void setQuestion42Form(Question42Form question42Form) {
        this.question42Form = question42Form;
    }

    public Question43Form getQuestion43Form() {
        return question43Form;
    }

    public void setQuestion43Form(Question43Form question43Form) {
        this.question43Form = question43Form;
    }

    public Question44Form getQuestion44Form() {
        return question44Form;
    }

    public void setQuestion44Form(Question44Form question44Form) {
        this.question44Form = question44Form;
    }

    public Question45Form getQuestion45Form() {
        return question45Form;
    }

    public void setQuestion45Form(Question45Form question45Form) {
        this.question45Form = question45Form;
    }

    public Question35Form getQuestion35Form() {
        return question35Form;
    }

    public void setQuestion35Form(Question35Form question35Form) {
        this.question35Form = question35Form;
    }

    public Question36Form getQuestion36Form() {
        return question36Form;
    }

    public void setQuestion36Form(Question36Form question36Form) {
        this.question36Form = question36Form;
    }

    public Question104Form getQuestion104Form() {
        return question104Form;
    }

    public void setQuestion104Form(Question104Form question104Form) {
        this.question104Form = question104Form;
    }

    public Question39Form getQuestion39Form() {
        return question39Form;
    }

    public void setQuestion39Form(Question39Form question39Form) {
        this.question39Form = question39Form;
    }

    public Question99Form getQuestion99Form() {
        return question99Form;
    }

    public void setQuestion99Form(Question99Form question99Form) {
        this.question99Form = question99Form;
    }

    public AddressForm getQuestion100Form() {
        return question100Form;
    }

    public void setQuestion100Form(AddressForm question100Form) {
        this.question100Form = question100Form;
    }

    public Question101Form getQuestion101Form() {
        return question101Form;
    }

    public void setQuestion101Form(Question101Form question101Form) {
        this.question101Form = question101Form;
    }

    public Question103Form getQuestion103Form() {
        return question103Form;
    }

    public void setQuestion103Form(Question103Form question103Form) {
        this.question103Form = question103Form;
    }
}
