package kiosk.session;

import kiosk.model.Order;

/** Simple holder so every controller shares the same active order */
public final class OrderSession {
    private static final Order ACTIVE_ORDER = new Order();
    private OrderSession(){}
    public static Order get(){ return ACTIVE_ORDER; }
}
