/**
 *
 */
package com.affirm.client.model;

import com.affirm.common.model.catalog.Bot;

/**
 * @author jrodriguez
 *         <p>
 *         This class is used for execute an action when all the bots especified
 *         ends
 */
public class BotsAsyncFinishedListener {

    private boolean sunatBotFinished = false;
    private boolean reniecBotFinished = false;
    private boolean essaludBotFinished = false;
    private boolean redamBotFinished = false;
    private boolean claroBotFinished = false;
    private boolean movistarBotFinished = false;
    private boolean bitelBotFinished = false;
    public IBotsAsyncFinishedListener listener;

    public void finish(int botId) {
        if (botId == Bot.SUNAT_BOT) {
            sunatBotFinished = true;
        } else if (botId == Bot.RENIEC_BOT) {
            reniecBotFinished = true;
        } else if (botId == Bot.ESSALUD_BOT) {
            essaludBotFinished = true;
        } else if (botId == Bot.REDAM_BOT) {
            redamBotFinished = true;
        } else if (botId == Bot.CLARO) {
            claroBotFinished = true;
        } else if (botId == Bot.MOVISTAR) {
            movistarBotFinished = true;
        } else if (botId == Bot.BITEL) {
            bitelBotFinished = true;
        }

        if (sunatBotFinished && reniecBotFinished && essaludBotFinished && redamBotFinished && movistarBotFinished && bitelBotFinished) {
            listener.onFinished();
        }
    }

    public boolean isSunatBotFinished() {
        return sunatBotFinished;
    }

    public void setSunatBotFinished(boolean sunatBotFinished) {
        this.sunatBotFinished = sunatBotFinished;
    }

    public boolean isReniecBotFinished() {
        return reniecBotFinished;
    }

    public void setReniecBotFinished(boolean reniecBotFinished) {
        this.reniecBotFinished = reniecBotFinished;
    }

    public boolean isEssaludBotFinished() {
        return essaludBotFinished;
    }

    public void setEssaludBotFinished(boolean essaludBotFinished) {
        this.essaludBotFinished = essaludBotFinished;
    }

    public boolean isRedamBotFinished() {
        return redamBotFinished;
    }

    public void setRedamBotFinished(boolean redamBotFinished) {
        this.redamBotFinished = redamBotFinished;
    }

    public boolean isClaroBotFinished() {
        return claroBotFinished;
    }

    public void setClaroBotFinished(boolean claroBotFinished) {
        this.claroBotFinished = claroBotFinished;
    }

    public boolean isMovistarBotFinished() {
        return movistarBotFinished;
    }

    public void setMovistarBotFinished(boolean movistarBotFinished) {
        this.movistarBotFinished = movistarBotFinished;
    }

    public boolean isBitelBotFinished() {
        return bitelBotFinished;
    }

    public void setBitelBotFinished(boolean bitelBotFinished) {
        this.bitelBotFinished = bitelBotFinished;
    }

    public IBotsAsyncFinishedListener getListener() {
        return listener;
    }

    public void setListener(IBotsAsyncFinishedListener listener) {
        this.listener = listener;
    }

    public interface IBotsAsyncFinishedListener {
        void onFinished();
    }

}
