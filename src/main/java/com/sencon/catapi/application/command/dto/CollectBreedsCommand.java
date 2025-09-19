package com.sencon.catapi.application.command.dto;

public class CollectBreedsCommand {
    
    private boolean forceUpdate;
    
    public CollectBreedsCommand() {
        this.forceUpdate = false;
    }
    
    public CollectBreedsCommand(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
    
    public boolean isForceUpdate() {
        return forceUpdate;
    }
    
    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
