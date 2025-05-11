package com.app.network;

public interface IServer{
    public abstract void processRequest(UserHandler userHandler, String message);
    public abstract void disconnectUser(UserHandler userHandler);
}
