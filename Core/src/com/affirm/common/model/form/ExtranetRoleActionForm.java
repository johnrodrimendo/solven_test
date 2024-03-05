package com.affirm.common.model.form;

import com.affirm.common.model.transactional.ExtranetMenuRoleGroup;

import java.util.ArrayList;
import java.util.List;

public class ExtranetRoleActionForm {

    private List<Value> values;

    public void addValue(Value value) {
        if (values == null)
            values = new ArrayList<>();
        values.add(value);
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public static class Value {

        private Integer extranetMenuId;
        private Integer menuId;
        private List<ExtranetMenuRoleGroup> roleGroups = new ArrayList<>();
        private List<Integer> menuEntityProductCategories = new ArrayList<>();

        public Integer getExtranetMenuId() {
            return extranetMenuId;
        }

        public void setExtranetMenuId(Integer extranetMenuId) {
            this.extranetMenuId = extranetMenuId;
        }

        public List<ExtranetMenuRoleGroup> getRoleGroups() {
            return roleGroups;
        }

        public void setRoleGroups(List<ExtranetMenuRoleGroup> roleGroups) {
            this.roleGroups = roleGroups;
        }

        public List<Integer> getMenuEntityProductCategories() {
            return menuEntityProductCategories;
        }

        public void setMenuEntityProductCategories(List<Integer> menuEntityProductCategories) {
            this.menuEntityProductCategories = menuEntityProductCategories;
        }
    }
}
