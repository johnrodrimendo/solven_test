package com.affirm.client.model.form;

import com.affirm.common.model.catalog.EntityUserType;
import com.affirm.common.util.*;

import java.io.Serializable;

// TODO: MOVER A CORE/BDS/MODELS??
public class RegisterEntityUserRolesForm extends FormGeneric implements Serializable {

    private Integer id;
    private String name;
    private String firstSurname;
    private String email;
    private Integer roleId;
    private Integer organizerId;
    private Integer producerId;
    private Integer idInEntity;
    private Integer channelId;

    public RegisterEntityUserRolesForm() {
        this.setValidator(new RegisterEntityUserRolesForm.RegisterEntityUserFormValidator());
    }

    public static RegisterEntityUserRolesForm newInstance() {
        return new RegisterEntityUserRolesForm();
    }

    public class RegisterEntityUserFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator name;
        public StringFieldValidator firstSurname;
        public StringFieldValidator email;
        public IntegerFieldValidator roleId;
        public IntegerFieldValidator organizerId;
        public IntegerFieldValidator producerId;
        public IntegerFieldValidator idInEntity;
        public IntegerFieldValidator channelId;

        public RegisterEntityUserFormValidator() {
            addValidator(name = new StringFieldValidator(ValidatorUtil.NAME));
            addValidator(firstSurname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setMaxCharacters(60));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
            addValidator(roleId = new IntegerFieldValidator().setFieldName("Rol").setRequired(true));
            addValidator(organizerId = new IntegerFieldValidator().setFieldName("Organizador").setRequired(false));
            addValidator(producerId = new IntegerFieldValidator().setFieldName("Productor").setRequired(false));
            addValidator(idInEntity = new IntegerFieldValidator().setFieldName("Id en la entidad").setRequired(false));
            addValidator(channelId = new IntegerFieldValidator().setFieldName("Canal").setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {
            if(roleId.getValue() == EntityUserType.BDS_USUARIO)
                producerId.setRequired(true);
            else if(roleId.getValue() == EntityUserType.BDS_PRODUCTOR)
                organizerId.setRequired(true);

            if (roleId.getValue() == EntityUserType.BDS_USUARIO ||
                    roleId.getValue() == EntityUserType.BDS_PRODUCTOR ||
                    roleId.getValue() == EntityUserType.BDS_ORGANIZADOR) {
                idInEntity.setRequired(true);
                channelId.setRequired(true);
            } else {
                channelId.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return RegisterEntityUserRolesForm.this;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
    }

    public Integer getProducerId() {
        return producerId;
    }

    public void setProducerId(Integer producerId) {
        this.producerId = producerId;
    }

    public Integer getIdInEntity() {
        return idInEntity;
    }

    public void setIdInEntity(Integer idInEntity) {
        this.idInEntity = idInEntity;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }
}
