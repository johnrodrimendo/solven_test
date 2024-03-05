/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.ContractScheduleService;
import org.springframework.stereotype.Service;

/**
 * @author jrodriguez
 */
@Service("contractScheduleService")
public class ContractScheduleServiceImpl implements ContractScheduleService {

    @Override
    public String getGastosComisionesHeader(Integer entityId){
        if(entityId != null){
            switch (entityId){
                case Entity.RIPLEY:
                    return "Protección <br/> de pagos";
                case Entity.FINANSOL:
                    return "Aportes";
                case Entity.PRISMA:
                    return "Aportes <br/> societarios";
            }
        }
        return "Gastos y <br/> Comisiones";
    }

}
