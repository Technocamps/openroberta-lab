package de.fhg.iais.roberta.persistence.bo;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.fhg.iais.roberta.util.Util;
import de.fhg.iais.roberta.util.dbc.Assert;

@Entity
@Table(name = "USERGROUP")
public class UserGroup implements WithSurrogateId {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private User owner;

    @Column(name = "NAME")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCESS_RIGHT")
    private AccessRight accessRight;

    @Column(name = "CREATED")
    private Timestamp created;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private Set<User> members;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USERGROUP_ROBOTS", joinColumns = @JoinColumn(name = "USERGROUP_ID"), inverseJoinColumns = @JoinColumn(name = "ROBOT_ID"))
    private Set<Robot> robots;

    /*
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USERGROUP_PROGRAMS", joinColumns = @JoinColumn(name = "USERGROUP_ID"), inverseJoinColumns = @JoinColumn(name = "PROGRAM_ID"))
    private Set<Program> programs;
    
    
    public Set<Program> getPrograms() {
        if ( this.programs == null ) {
            this.programs = new HashSet<>();
        }
        return new HashSet<>(this.programs);
    }
    */

    protected UserGroup() {
        // Hibernate
    }

    /**
     * create a new group
     *
     * @param name the name of the group, not null
     * @param owner the user who created and thus owns the group
     */
    public UserGroup(String name, User owner) {
        Assert.notNull(name);
        Assert.notNull(owner);

        this.name = name;
        this.owner = owner;
        this.accessRight = AccessRight.ALL_READ;
        this.created = Util.getNow();
    }

    @Override
    public int getId() {
        return this.id;
    }

    /**
     * get the name of the group
     *
     * @return the name, never <code>null</code>
     */

    public String getName() {
        return this.name;
    }

    /**
     * get the access right of the programs related to the group
     *
     * @return the access right, never <code>null</code>
     */

    public AccessRight getAccessRight() {
        return this.accessRight;
    }

    /**
     * set the access right of the programs related to the group
     */

    public void setAccessRight(AccessRight accessRight) {
        //TODO: Discuss to set default AccessRight in case of null
        Assert.notNull(accessRight);
        this.accessRight = accessRight;
    }

    /**
     * get the owner
     *
     * @return the owner, never <code>null</code>
     */
    public User getOwner() {
        return this.owner;
    }

    public Timestamp getCreated() {
        return this.created;
    }

    public void addRobot(Robot robot) {
        if ( this.robots == null ) {
            this.robots = new HashSet<>();
        }
        this.robots.add(robot);
    }

    public void removeRobot(Robot robot) {
        if ( this.robots == null ) {
            this.robots = new HashSet<>();
        } else {
            this.robots.remove(robot);
        }
    }

    public Set<Robot> getRobots() {
        if ( this.robots == null ) {
            this.robots = new HashSet<>();
        }
        return new HashSet<>(this.robots);
    }

    public void addMember(User member) {
        if ( this.members == null ) {
            this.members = new HashSet<>();
        }
        this.members.add(member);
    }

    public void removeMember(User member) {
        if ( this.members == null ) {
            this.members = new HashSet<>();
        } else {
            this.members.remove(member);
        }
    }

    public Set<User> getMembers() {
        if ( this.members == null ) {
            this.members = new HashSet<>();
        }
        return new HashSet<>(this.members);
    }

    @Override
    public String toString() {
        return "UserGroup [id="
            + this.id
            + ", name="
            + this.name
            + ", ownerId="
            + (this.owner == null ? "???" : this.owner.getId())
            + ", created="
            + this.created
            + "]";
    }

    public void rename(String newGroupName) {
        Assert.notNull(newGroupName);
        this.name = newGroupName;
    }

    /**
     * Converts the UserGroup Object into a JSON representation, which's structure is optimized for showing it in a front end list
     *
     * @return JSObject A JSON representation of the UserGroup object
     */
    public JSONObject toListJSON() {
        JSONObject jsonObject = new JSONObject(), tmp;
        JSONArray members = new JSONArray(), programs = new JSONArray(), robots = new JSONArray();

        /*
         * TODO: Implement and uncomment
        for ( Program program : this.getPrograms() ) {
            try {
                tmp = new JSONObject();
                tmp.put("id", program.getId());
                tmp.put("name", program.getName());
                programs.put(tmp);
            } catch ( JSONException e ) {
                return null;
            }
        }
        */

        for ( User user : this.getMembers() ) {
            try {
                tmp = new JSONObject();
                //TODO: Call method in USer class, do not re implement it here. Or create a new method in the JSON UTIL? Discuss with team
                tmp.put("id", user.getId());
                tmp.put("account", user.getAccount());
                tmp.put("hasDefaultPassword", user.isPasswordCorrect(user.getAccount()));
                members.put(tmp);
            } catch ( Exception e ) {
                //Either a JSONException, or a general Exception in case of the Password check
                return null;
            }
        }

        for ( Robot robot : this.getRobots() ) {
            try {
                tmp = new JSONObject();
                //TODO: Call method in USer class, do not re implement it here. Or create a new method in the JSON UTIL? Discuss with team
                tmp.put("id", robot.getId());
                tmp.put("name", robot.getName());
                robots.put(tmp);
            } catch ( Exception e ) {
                //Either a JSONException, or a general Exception in case of the Password check
                return null;
            }
        }

        //TODO: Count number of members
        //TODO: Add list of shared programs from owner

        try {
            jsonObject.put("name", this.getName());
            jsonObject.put("owner", this.getOwner().getAccount());
            jsonObject.put("created", this.getCreated());
            jsonObject.put("members", members);
            jsonObject.put("programs", programs);
            jsonObject.put("robots", robots);
        } catch ( JSONException e ) {
            return null;
        }
        return jsonObject;
    }
}
