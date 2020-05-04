define([ 'exports', 'log', 'message', 'comm', 'util', 'userGroup.model', 'guiState.controller', 'jquery', 'blocks-msg', 'bootstrap-table', 'bootstrap-tagsinput', 'blocks' ], function(exports, LOG, MSG, COM, UTIL, USERGROUP,
        GUISTATE_C, $, Blockly) {
    
    //TODO: If user logs out and is in one of this views, change to program view
    
    function showPanel() {
        $('#userGroupTable').bootstrapTable('showLoading');
        USERGROUP.loadUserGroupList(function(data) {
            if (data.rc === 'ok') {
                $('#userGroupTable').bootstrapTable('load', data.userGroups);
            } else {
                $('#userGroupTable').bootstrapTable('removeAll');
                MSG.displayInformation(data, data.cause, data.cause);
            }
            $('#userGroupTable').bootstrapTable('hideLoading');
        })
        $('#tabUserGroupList').click();
        guiStateController.setView('tabUserGroupList');
    }
    exports.showPanel = showPanel;
    
    /**
     * Initialize table of tutorials
     */
    function init() {
        initUserGroupList();
        initProgListEvents();
        LOG.info('init UserGroup list view');
    }
    exports.init = init;

    function initUserGroupList() {
        var $actionItemsTemplate = $('#userGroupTable .action-items-template'),
            $memberPasswordResetTemplate = $('#userGroupMemberTable .reset-password-template'),
            $memberActionItemsTemplate = $('#userGroupMemberTable .action-items-template');

        $actionItemsTemplate.remove();
        
        $('#userGroupTable').bootstrapTable({
            height : UTIL.calcDataTableHeight(),
            pageList : '[ 10, 25, All ]',
            toolbar : '#userGroupListToolbar',
            toolbarAlign: 'none',
            showRefresh : true,
            sortName : 'created',
            sortOrder : 'desc',
            showPaginationSwitch : true,
            pagination : true,
            buttonsAlign : 'right',
            resizable : true,
            iconsPrefix : 'typcn',
            icons : {
                paginationSwitchDown : 'typcn-document-text',
                paginationSwitchUp : 'typcn-book',
                refresh : 'typcn-refresh',
            },
            columns : [ {
                title : "<span lkey='Blockly.Msg.DATATABLE_USERGROUP_NAME'>" + (Blockly.Msg.DATATABLE_USERGROUP_NAME || "Name der Gruppe") + "</span>",
                field: 'name',
                sortable : true,
            }, {
                title : "<span lkey='Blockly.Msg.DATATABLE_MEMBERS'>" + (Blockly.Msg.DATATABLE_MEMBERS || "Mitglieder") + "</span>",
                field: 'members',
                events : {
                    'click td' : function(e, value, row, index) {
                        e.stopPropagation();
                      //TODO: Show a list of all members of the group on click
                    }
                },
                sortable : true,
                sorter : function(a, b) {
                    //TODO: Sort list by members count
                    return 0;
                },
                formatter : function(value, row, index) {
                    //TODO: Convert a list of members to a number
                    return 0;
                },
                align : 'left',
                valign : 'middle',
            }, {
                title : "<span lkey='Blockly.Msg.DATATABLE_SHARED_PROGRAMS'>" + (Blockly.Msg.DATATABLE_MEMBERS || "Geteilte Programme") + "</span>",
                field: 'programs',
                events : {
                    'click td' : function(e, value, row, index) {
                        e.stopPropagation();
                        //TODO: Show a pop-up with the programs that the user shares with the members of the group on click
                    }
                },
                sortable : false,
                formatter : function(value, row, index) {
                    //TODO: Show the flow-merge symbol for each row. Maybe add an indicator if there are no programs shared at all
                    return 0;
                },
                align : 'left',
                valign : 'middle',
            }, {
                title : "<span lkey='Blockly.Msg.DATATABLE_CREATED_ON'>" + (Blockly.Msg.DATATABLE_CREATED_ON || "Erzeugt am") + "</span>",
                field: 'created',
                sortable : true,
                formatter : UTIL.formatDate
            }, {
                checkbox : true,
                valign : 'middle',
            }, {
                title : '<a href="#" id="deleteUserGroups" class="delete-usergroups disabled" rel="tooltip" lkey="Blockly.Msg.PROGLIST_DELETE_ALL_TOOLTIP" data-original-title="" data-container="body" title="">'
                    + '<span class="typcn typcn-delete"></span></a>', //TODO: Rework for user-groups mass delete
                events : {
                    'click .delete' : function(e, value, row, index) {
                        e.stopPropagation();
                        
                        var groupName = typeof row.name === undefined ? null : row.name,
                            groupMembers = typeof row.members === undefined ? 0 : row.members,
                            deleteFunction = function () {
                                USERGROUP.deleteUserGroup(row.name, function (data) {
                                    if (data.rc === 'ok') {
                                        $('#userGroupTable').bootstrapTable('remove', {field: 'name', values: [row.name]})
                                    } else {
                                        MSG.displayInformation(data, data.cause, data.cause);
                                    }
                                });
                            };
                        
                        if (groupName === null) {
                            return;
                        }
                        
                        if (groupMembers == 0) {
                            deleteFunction();
                            return;
                        }
                        
                        
                        
                        USERGROUP.deleteUserGroup(row.name, function (data) {
                            if (data.rc === 'ok') {
                                $('#userGroupTable').bootstrapTable('remove', {field: 'name', values: [row.name]})
                            } else {
                                MSG.displayInformation(data, data.cause, data.cause);
                            }
                        });
                    },
                    'click .share': function(e, value, row, index) {
                        e.stopPropagation();
                        //TODO: Share programs Popup
                        debugger;
                    }
                },
                align : 'left',
                valign : 'top',
                formatter : function(value, row, index) {
                    return $actionItemsTemplate.find('td').html();
                },
                width : '117px',
            }, ]
        });
        
        $('#userGroupMemberTable').bootstrapTable({
            height : UTIL.calcDataTableHeight(),
            pageList : '[ 10, 25, All ]',
            toolbar : '#userGroupMemberListToolbar',
            toolbarAlign: 'none',
            showRefresh : true,
            sortName : 'name',
            sortOrder : 'asc',
            showPaginationSwitch : true,
            pagination : true,
            buttonsAlign : 'right',
            resizable : true,
            iconsPrefix : 'typcn',
            icons : {
                paginationSwitchDown : 'typcn-document-text',
                paginationSwitchUp : 'typcn-book',
                refresh : 'typcn-refresh',
            },
            columns : [ {
                title : "<span lkey='Blockly.Msg.POPUP_USERNAME'>" + (Blockly.Msg.POPUP_USERNAME || "Username") + "</span>",
                field: 'name',
                sortable : true,
            }, {
                title : "<span lkey='Blockly.Msg.RESET_PASSWORD'>" + (Blockly.Msg.RESET_PASSWORD || "Reset password") + "</span>",
                events : {
                    'click .reset-password' : function(e, value, row, index) {
                        e.stopPropagation();
                      //TODO: Call REST resource to reset the password
                        debugger;
                    }
                },
                sortable : false,
                formatter : function(value, row, index) {
                    return $memberPasswordResetTemplate.find('td').html();
                },
                align : 'center',
                valign : 'middle',
            }, {
                checkbox : true,
                valign : 'middle',
            }, {
                title : '<a href="#" id="deleteUserGroupMembers" class="delete-user-group-members disabled" rel="tooltip" lkey="Blockly.Msg.PROGLIST_DELETE_ALL_TOOLTIP" data-original-title="" data-container="body" title="">'
                    + '<span class="typcn typcn-delete"></span></a>', //TODO: Rework for user-groups-member mass delete
                events : {
                    'click .delete' : function(e, value, row, index) {
                        e.stopPropagation();
                        
                        debugger;
                        //TDO: Add REST resource, then add here
                        
                        var groupName = typeof row.name === undefined ? null : row.name,
                            groupMembers = typeof row.members === undefined ? 0 : row.members,
                            deleteFunction = function () {
                                USERGROUP.deleteUserGroup(row.name, function (data) {
                                    if (data.rc === 'ok') {
                                        $('#userGroupTable').bootstrapTable('remove', {field: 'name', values: [row.name]})
                                    } else {
                                        MSG.displayInformation(data, data.cause, data.cause);
                                    }
                                });
                            };
                        
                        if (groupName === null) {
                            return;
                        }
                        
                        if (groupMembers == 0) {
                            deleteFunction();
                            return;
                        }
                        
                        USERGROUP.deleteUserGroup(row.name, function (data) {
                            if (data.rc === 'ok') {
                                $('#userGroupTable').bootstrapTable('remove', {field: 'name', values: [row.name]})
                            } else {
                                MSG.displayInformation(data, data.cause, data.cause);
                            }
                        });
                    }
                },
                align : 'left',
                valign : 'top',
                formatter : function(value, row, index) {
                    return $memberActionItemsTemplate.find('td').html();
                },
                width : '117px',
            }, ]
        });
    }
    exports.initUserGroupList = initUserGroupList;
    

    function initProgListEvents() {
        $(window).resize(function() {
            $('#userGroupTable').bootstrapTable('resetView', {
                height : UTIL.calcDataTableHeight()
            });
        });
        
        $('#backUserGroupList').onWrap('click', function() {
            $('#tabProgram').click();
            return false;
        }, "back to program view");
        
        $('#showCreateUserGroupPopup').click(function() {
            $('#create-user-group').modal('show');
            return false;
        });
        
        $('#create-user-group .close-button').click(function() {
            $('#create-user-group').modal('hide');
            return false;
        });
        
        $('#userGroupNameInput').keydown(function() {
            $('#create-user-group .hint').hide();
        });
        
        $('#create-user-group .accept-button').click(function() {
            var groupName = $('#userGroupNameInput').val();
            USERGROUP.createUserGroup(groupName, function (data) {
                switch(data.cause) {
                    case 'ORA_GROUP_CREATE_SUCCESS':
                        //TODO: Add group to current list
                        var values = {
                                name: groupName,
                                members: 0, 
                                programs: [],
                                created: UTIL.formatDate(Date.now())
                            },
                            tableData = $('#userGroupTable').bootstrapTable("getData");
                        
                        //Clone array, because the original array is directly linked to the bootstrap table.
                        //No need to clone the items in it, though, normal reference copy is enough.
                        tableData = tableData.map(function (item) {return item;});
                        tableData.unshift(values);
                        $('#userGroupTable').bootstrapTable("showLoading");
                        $('#userGroupTable').bootstrapTable('removeAll');
                        $('#userGroupTable').bootstrapTable("load", tableData);
                        $('#userGroupTable').bootstrapTable("hideLoading");
                        $('#create-user-group .hint').hide();
                        $('#userGroupNameInput').val('');
                        $('#create-user-group').modal('hide');
                        break;
                    case 'ORA_GROUP_ERROR_MISSING_RIGHTS_TO_BE_GROUP_OWNER':
                    case 'ORA_GROUP_CREATE_ERROR_GROUP_LIMIT_REACHED':
                    case 'ORA_GROUP_ERROR_NAME_INVALID':
                    case 'ORA_GROUP_CREATE_ERROR_GROUP_ALREADY_EXISTS':
                        $('#create-user-group .hint').attr('lkey', 'Blockly.Msg.' + data.cause);
                        $('#create-user-group .hint').text(Blockly.Msg[data.cause] || data.cause);
                        $('#create-user-group .hint').show();
                        break;
                    default:
                        //TODO: Show error message in popup
                        MSG.displayInformation(data, data.cause, data.cause);
                        
                }
            });
            return false;
        });
        
        $('#userGroupTable').onWrap('click-row.bs.table', function(e, rowData, row) {
            openDetailUserGroupView(rowData, row.data('index'));
        }, "Load program from listing clicked");
        
    }
    exports.initProgListEvents = initProgListEvents;
    
    function openDetailUserGroupView (userGroupData, userGroupListIndex) {
        if (typeof userGroupData.name === undefined) {
            MSG.displayPopupMessage('ORA_GROUP_GET_MEMBERS_ERROR', 'Could not open group detail view.', 'OK');
            return;
        }
        
        $('#userGroupMemberListHeader').html(userGroupData.name.trim() || '&nbsp;');
        
        $('#userGroupMemberTable').bootstrapTable('showLoading');
        $('#userGroupMemberTable').bootstrapTable('removeAll');
        USERGROUP.loadUserGroupMemberList(userGroupData.name, function(data) {
            if (data.rc === 'ok') {
                $('#userGroupMemberTable').bootstrapTable('load', data.members);
            } else {
                MSG.displayInformation(data, data.cause, data.cause);
            }
            $('#userGroupMemberTable').bootstrapTable('hideLoading');
        })
        $('#tabUserGroupMemberList').click();
        guiStateController.setView('tabUserGroupMemberList');
    }
});
