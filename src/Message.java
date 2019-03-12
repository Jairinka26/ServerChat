/*
 * Copyright (c) 1997-2013 InfoReach, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * InfoReach ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with InfoReach.
 *
 * CopyrightVersion 2.0
 */

/**
 * TODO: add description
 *
 * @author Irina.Paschenko
 */
public class Message {
    private String message;

    String buildMessage (String fromClient,String message){
        this.message = fromClient.concat(" >> ").concat(message);
        return this.message;
    }


}
