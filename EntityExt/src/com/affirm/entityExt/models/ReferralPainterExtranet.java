/**
 *
 */
package com.affirm.entityExt.models;

import com.affirm.common.model.catalog.TrackingAction;
import com.affirm.common.model.transactional.LoanApplicationTrackingAction;
import com.affirm.common.model.transactional.Referral;

import java.util.List;

/**
 * @author jrodriguez
 */
public class ReferralPainterExtranet extends Referral {

    private List<LoanApplicationTrackingAction> trackingActions;


    public TrackingAction getLastTrackingAction() {
        return trackingActions == null || trackingActions.isEmpty() ? null : trackingActions.get(0).getTrackingAction();
    }

    public List<LoanApplicationTrackingAction> getTrackingActions() {
        return trackingActions;
    }

    public void setTrackingActions(List<LoanApplicationTrackingAction> trackingActions) {
        this.trackingActions = trackingActions;
    }
}

