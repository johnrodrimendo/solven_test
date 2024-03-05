package com.affirm.efl.model;

import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.PersonOcupationalInformation;

/**
 * Created by dev5 on 04/07/17.
 */
public class AddressObjectList {

    private AddressObject business;
    private AddressObject home;
    private AddressObject work;

    public AddressObjectList(PersonContactInformation contactInfo, PersonOcupationalInformation ocupationalInfo){
        this.home = new AddressObject(contactInfo);
        this.work = new AddressObject(ocupationalInfo);
    }

}
