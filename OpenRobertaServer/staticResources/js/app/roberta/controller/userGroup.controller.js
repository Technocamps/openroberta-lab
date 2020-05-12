define([ 'exports', 'log', 'message', 'comm', 'util', 'userGroup.model', 'guiState.controller', 'jquery', 'blocks-msg', 'bootstrap-table', 'bootstrap-tagsinput', 'blocks' ], function(exports, LOG, MSG, COM, UTIL, USERGROUP,
        GUISTATE_C, $, Blockly) {
    
    //TODO: If user logs out and is in one of this views, change to program view
    var $userGroupTable;
    
    function showPanel() {
        $userGroupTable.bootstrapTable('showLoading');
        USERGROUP.loadUserGroupList(function(data) {
            if (data.rc === 'ok') {
                $userGroupTable.bootstrapTable('load', data.userGroups);
            } else {
                $userGroupTable.bootstrapTable('removeAll');
                MSG.displayInformation(data, data.cause, data.cause);
            }
            $userGroupTable.bootstrapTable('hideLoading');
        })
        $('#tabUserGroupList').click();
        guiStateController.setView('tabUserGroupList');
    }
    exports.showPanel = showPanel;
    
    /**
     * Initialize table of tutorials
     */
    function init() {
        $userGroupTable = $('#userGroupTable');
        
        initUserGroupListTable();
        initUserGroupEvents();
        
        initUserGroupMemberListTable();
        initUserGroupMemberEvents();
        
        LOG.info('UserGroup list-view initialized.');
    }
    exports.init = init;
    
    function initUserGroupListTable() {
        var $actionItemsTemplate = $userGroupTable.find('.action-items-template');
    
        $actionItemsTemplate.remove();
        
        $userGroupTable.bootstrapTable({
            height : UTIL.calcDataTableHeight(),
            pageList : '[ 10, 25, All ]',
            toolbar : '#userGroupListToolbar',
            toolbarAlign: 'none',
            showRefresh : true,
            sortName : 'created',
            sortOrder : 'desc',
            showPaginationSwitch : true,
            pagination : false,
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
                sortable : true,
                sorter : function(a, b) {
                    return (a.length || 0) - (b.length || 0);
                },
                formatter : function(value, row, index) {
                    return value.length || 0;
                }
            }, {
                title : "<span lkey='Blockly.Msg.DATATABLE_SHARED_PROGRAMS'>" + (Blockly.Msg.DATATABLE_MEMBERS || "Geteilte Programme") + "</span>",
                field: 'programs',
                sortable : false,
                formatter : function(value, row, index) {
                    return value.length || 0;
                }
            }, {
                title : "<span lkey='Blockly.Msg.DATATABLE_CREATED_ON'>" + (Blockly.Msg.DATATABLE_CREATED_ON || "Erzeugt am") + "</span>",
                field: 'created',
                sortable : true,
                formatter : UTIL.formatDate
            }, {
                checkbox : true,
                valign : 'middle',
            }, {
                title : '<a href="#" id="deleteUserGroups" class="deleteSome disabled" rel="tooltip" lkey="Blockly.Msg.PROGLIST_DELETE_ALL_TOOLTIP" data-original-title="" data-container="body" title="">'
                    + '<span class="typcn typcn-delete"></span></a>',
                events : {
                    'click .delete' : function(e, value, row, index) {
                        e.stopPropagation();
                        
                        var groupName = typeof row.name === undefined ? null : row.name,
                            groupMembers = typeof row.members === undefined ? [] : row.members,
                            deleteFunction = function (deleteMembers) {
                                USERGROUP.deleteUserGroup(row.name, deleteMembers, function (data) {
                                    if (data.rc === 'ok') {
                                        $userGroupTable.bootstrapTable('remove', {field: 'name', values: [row.name]})
                                    } else {
                                        MSG.displayInformation(data, data.cause, data.cause);
                                    }
                                });
                            };
                        
                        if (groupName === null) {
                            return;
                        }
                        
                        if (!groupMembers.length) {
                            deleteFunction(false);
                            return;
                        } else if (groupMembers.filter(function (groupMember) {return !groupMember.hasDefaultPassword;}).length === 0) {
                            var modalMessageKey = 'USERGROUP_DELETE_WITH_MEMBERS_WARNING',
                                modalMessage = Blockly.Msg[modalMessageKey] || 'Are your sure that you want to delete the usergroup including all members? No member did log in so far.';
                            $('#show-message-confirm').one('shown.bs.modal', function(e) {
                                $('#confirm').off();
                                $('#confirm').on('click', function(e) {
                                    e.preventDefault();
                                    deleteFunction(true);
                                });
                                $('#confirmCancel').off();
                                $('#confirmCancel').on('click', function(e) {
                                    e.preventDefault();
                                    $('.modal').modal('hide');
                                });
                            });
                            MSG.displayPopupMessage(modalMessageKey, modalMessage, 'OK', Blockly.Msg.POPUP_CANCEL);
                        } else {
                            MSG.displayInformation({}, '', 'ORA_GROUP_DELETE_ERROR_GROUP_HAS_MEMBERS');
                        }
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
    }
    /* This is an internal function and a part of the initialization. Do not export it. */
    

    function initUserGroupEvents() {
        $(window).resize(function() {
            $userGroupTable.bootstrapTable('resetView', {
                height : UTIL.calcDataTableHeight()
            });
        });
        
        $userGroupTable.closest('#userGroupList').find('button[name="refresh"]').onWrap('click', function(evt) {
            evt.preventDefault();
            showPanel();
        }, 'refreshed usergroup view');
        
        $userGroupTable.onWrap('check-all.bs.table', function($element, rows) {
            $userGroupTable.closest('#userGroupList').find('.deleteSome').removeClass('disabled');
            $userGroupTable.find('.delete').addClass('disabled');
            $userGroupTable.find('.share').addClass('disabled');
        }, 'check all usergroups');

        $userGroupTable.onWrap('check.bs.table', function($element, row) {
            $userGroupTable.closest('#userGroupList').find('.deleteSome').removeClass('disabled');
            $userGroupTable.find('.delete').addClass('disabled');
            $userGroupTable.find('.share').addClass('disabled');
        }, 'check one usergroup');

        $userGroupTable.onWrap('uncheck-all.bs.table', function($element, rows) {
            $userGroupTable.closest('#userGroupList').find('.deleteSome').addClass('disabled');
            $userGroupTable.find('.delete').removeClass('disabled');
            $userGroupTable.find('.share').removeClass('disabled');
        }, 'uncheck all usergroups');

        $userGroupTable.onWrap('uncheck.bs.table', function($element, row) {
            var selectedRows = $userGroupTable.bootstrapTable('getSelections');
            if (!selectedRows || selectedRows.length === 0) {
                $userGroupTable.closest('#userGroupList').find('.deleteSome').addClass('disabled');
                $userGroupTable.find('.delete').removeClass('disabled');
                $userGroupTable.find('.share').removeClass('disabled');
            }
        }, 'uncheck one usergroup');
        
        $userGroupTable.closest('#userGroupList').find('.deleteSome').onWrap('click', function() {
            var selectedRows = $userGroupTable.bootstrapTable('getSelections'),
                $deleteAllButton = $userGroupTable.closest('#userGroupList').find('.deleteSome');
            if (!selectedRows || selectedRows.length === 0 || $deleteAllButton.hasClass('disabled')) {
                return;
            }
            
            if (selectedRows.reduce(function(carry, element) {return carry + (element && element.members && element.members.length || 0);}, 0) > 0) {
                MSG.displayInformation({}, '', 'ORA_GROUP_DELETE_ERROR_GROUP_HAS_MEMBERS');
            } else {
                
                var errors = {},
                    removedRows = [],
                    callBackReceived = [];
                for(var i = 0; i < selectedRows.length; i++) {
                    USERGROUP.deleteUserGroup(selectedRows[i].name, function (data) {
                        if (data.rc === 'ok') {
                            removedRows.push(data.parameters.USERGROUP_NAME);
                        } else {
                            if (!errors[data.cause]) {
                                errors[data.cause] = [];
                            }
                            errors[data.cause].push(data.parameters.USERGROUP_NAME);
                        }
                        //TODO: Work with Promises instead
                        if (data.parameters.USERGROUP_NAME === selectedRows[selectedRows.length - 1].name) {
                            setTimeout(function() {
                                if (removedRows.length > 0) {
                                    $userGroupTable.bootstrapTable('remove', {field: 'name', values: removedRows})
                                }
                                
                                if (Object.keys(errors).length > 0) {
                                    var errorMessage = '';
                                    
                                    for (var errorKey in errors) {
                                        if (errors.hasOwnProperty(errorKey)) {
                                            errorMessage += '<p>\"' + errors[errorKey].join('", "') + '":<br/>' + (Blockly.Msg[errorKey] || errorKey) + '</p>';
                                        }
                                    }
                                    MSG.displayPopupMessage('', errorMessage, 'OK')
                                }
                            }, 500);
                        }
                    });
                }
            }
        }, 'Bulk delete usergroup');

        $('#backUserGroupList').onWrap('click', function() {
            $('#tabProgram').click();
            return false;
        }, "closed usergroup view and went back to program view.");
        
        $('#showCreateUserGroupPopup').click(function() {
            $('#create-user-group').modal('show');
            return false;
        });
        
        initCreateFormEvents();
        
        $userGroupTable.onWrap('click-row.bs.table', function(e, rowData, row) {
            openDetailUserGroupView(rowData, row.data('index'));
        }, "show usergroup member view");
        
    }
    /* This is an internal function and a part of the initialization. Do not export it. */
    
    function initCreateFormEvents() {

        $('#create-user-group .close-button').click(function() {
            $('#create-user-group').modal('hide');
            return false;
        });
        
        $('#userGroupNameInput').keydown(function() {
            $('#create-user-group label[for="createAccountName"] ~ .hint').hide();
        });
        
        $('#initialMembersInput').keydown(function() {
            $('#create-user-group label[for="initialMembersInput"] ~ .hint').hide();
        });
        
        $('#create-user-group .accept-button').click(function() {
            var groupName = $('#userGroupNameInput').val(),
                initialMembersCount = $('#initialMembersInput').val();
            
            if (initialMembersCount > 99) {
                $hint = $('#create-user-group label[for="initialMembersInput"] ~ .hint');
                $hint.attr('lkey', 'Blockly.Msg.ORA_GROUP_ADD_MEMBER_ERROR_LIMIT_REACHED');
                $hint.text(Blockly.Msg['ORA_GROUP_ADD_MEMBER_ERROR_LIMIT_REACHED'] || 'There can be no more than 99 members in a group.');
                $hint.show();
                return;
            }
            
            USERGROUP.createUserGroup(groupName, initialMembersCount, function (data) {
                var $hint;
                switch(data.cause) {
                    case 'ORA_GROUP_CREATE_SUCCESS':
                        //TODO: Add group to current list
                        var tableData = $userGroupTable.bootstrapTable("getData");
                        
                        //Clone array, because the original array is directly linked to the bootstrap table.
                        //No need to clone the items in it, though, normal reference copy is enough.
                        tableData = tableData.map(function (item) {return item;});
                        tableData.unshift(data.userGroup);
                        
                        $userGroupTable.bootstrapTable("showLoading");
                        $userGroupTable.bootstrapTable('removeAll');
                        $userGroupTable.bootstrapTable("load", tableData);
                        $userGroupTable.bootstrapTable("hideLoading");
                        
                        $('#create-user-group .hint').hide();
                        $('#create-user-group').modal('hide');
                        
                        $('#userGroupNameInput').val('');
                        $('#initialMembersInput').val(20)
                        break;
                    case 'ORA_GROUP_ERROR_MISSING_RIGHTS_TO_BE_GROUP_OWNER':
                    case 'ORA_GROUP_CREATE_ERROR_GROUP_LIMIT_REACHED':
                    case 'ORA_GROUP_ERROR_NAME_INVALID':
                    case 'ORA_GROUP_CREATE_ERROR_GROUP_ALREADY_EXISTS':
                        $hint = $('#create-user-group label[for="createAccountName"] ~ .hint');
                        $hint.attr('lkey', 'Blockly.Msg.' + data.cause);
                        $hint.text(Blockly.Msg[data.cause] || data.cause);
                        $hint.show();
                        break;
                    default:
                        //TODO: Show error message in popup
                        MSG.displayInformation(data, data.cause, data.cause);
                        
                }
            });
            return false;
        });
    }
    /* This is an internal function and a part of the initialization. Do not export it. */
    
    function initUserGroupMemberListTable() {
        var $memberPasswordResetTemplate = $('#userGroupMemberTable .reset-password-template'),
            $memberActionItemsTemplate = $('#userGroupMemberTable .action-items-template');
        
        $memberPasswordResetTemplate.remove();
        $memberActionItemsTemplate.remove();
        
        $('#userGroupMemberTable').bootstrapTable({
            height : UTIL.calcDataTableHeight(),
            pageList : '[ 10, 25, All ]',
            toolbar : '#userGroupMemberListToolbar',
            toolbarAlign: 'none',
            showRefresh : true,
            sortName : 'account',
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
                field: 'account',
                sortable : true,
            }, {
                title : "<span lkey='Blockly.Msg.RESET_PASSWORD'>" + (Blockly.Msg.RESET_PASSWORD || "Reset password") + "</span>",
                field: 'hasDefaultPassword',
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
                title : '<a href="#" id="deleteUserGroupMembers" class="delete-user-group-members disabled" lkey="Blockly.Msg.PROGLIST_DELETE_ALL_TOOLTIP" data-original-title="" data-container="body" title="">'
                    + '<span class="typcn typcn-delete"></span></a>', //TODO: Rework for user-groups-member mass delete
                events : {
                    'click .delete' : function(e, value, row, index) {
                        e.stopPropagation();
                        
                        debugger;
                        //TODO: Add REST resource, then add here
                        
                        var groupName = typeof row.name === undefined ? null : row.name,
                            groupMembers = typeof row.members === undefined ? 0 : row.members,
                            deleteFunction = function () {
                                USERGROUP.deleteUserGroup(row.name, function (data) {
                                    if (data.rc === 'ok') {
                                        $userGroupTable.bootstrapTable('remove', {field: 'name', values: [row.name]})
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
                                $userGroupTable.bootstrapTable('remove', {field: 'name', values: [row.name]})
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
    /* This is an internal function and a part of the initialization. Do not export it. */
    

    function initUserGroupMemberEvents() {
        $(window).resize(function() {
            $('#userGroupMemberTable').bootstrapTable('resetView', {
                height : UTIL.calcDataTableHeight()
            });
        });
        
        $('#backUserGroupMemberList').click(function() {
            showPanel();
            return false;
        });
        
    }
    /* This is an internal function and a part of the initialization. Do not export it. */
    
    function openDetailUserGroupView (userGroupData, userGroupListIndex) {
        if (userGroupData == null || typeof userGroupData.name !== 'string') {
            MSG.displayPopupMessage('ORA_GROUP_GET_MEMBERS_ERROR', 'Could not open group detail view for that group.', 'OK');
            return;
        }
        
        $('#userGroupMemberListHeader').html(userGroupData.name.trim() || '&nbsp;');
        
        $('#userGroupMemberTable').bootstrapTable('showLoading');
        $('#userGroupMemberTable').bootstrapTable('removeAll');
        $('#userGroupMemberTable').bootstrapTable('load', userGroupData.members);
        $('#userGroupMemberTable').bootstrapTable('hideLoading');
        $('#tabUserGroupMemberList').click();
        guiStateController.setView('tabUserGroupMemberList');
    }
});
