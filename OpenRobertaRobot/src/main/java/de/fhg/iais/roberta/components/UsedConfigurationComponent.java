package de.fhg.iais.roberta.components;

import de.fhg.iais.roberta.syntax.BlockType;

public class UsedConfigurationComponent {
    private final String port;
    private final BlockType type;
    private final String mode;

    public UsedConfigurationComponent(String port, BlockType type, String mode) {
        this.port = port;
        this.type = type;
        this.mode = mode;
    }

    public String getPort() {
        return this.port;
    }

    public BlockType getType() {
        return this.type;
    }

    public String getMode() {
        return this.mode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.mode == null) ? 0 : this.mode.hashCode());
        result = (prime * result) + ((this.port == null) ? 0 : this.port.hashCode());
        result = (prime * result) + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        UsedConfigurationComponent other = (UsedConfigurationComponent) obj;
        if ( this.mode == null ) {
            if ( other.mode != null ) {
                return false;
            }
        } else if ( !this.mode.equals(other.mode) ) {
            return false;
        }
        if ( this.port == null ) {
            if ( other.port != null ) {
                return false;
            }
        } else if ( !this.port.equals(other.port) ) {
            return false;
        }
        if ( this.type == null ) {
            if ( other.type != null ) {
                return false;
            }
        } else if ( !this.type.equals(other.type) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UsedConfigurationComponent [" + this.port + ", " + this.type + ", " + this.mode + "]";
    }
}
