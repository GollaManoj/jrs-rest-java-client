/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights  reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.importservice;

import com.jaspersoft.jasperserver.dto.importexport.ImportTask;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.AbstractAdapter;
import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.ThreadPoolUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.util.LinkedList;
import java.util.List;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;

public class ImportRequestAdapter extends AbstractAdapter {

    private static final String STATE_URI = "state";
    public static final String IMPORT_URI = "import";
    private String taskId;
    private ImportTask importTask;

    public ImportRequestAdapter(SessionStorage sessionStorage, String taskId) {
        super(sessionStorage);
        this.taskId = taskId;
        this.importTask = new ImportTask();
        importTask.setParameters(new LinkedList<String>());
    }

    public ImportRequestAdapter parameter(String parameter) {
        importTask.getParameters().add(parameter);
        return this;
    }

    public ImportRequestAdapter parameters(List<String> parameters) {
        importTask.getParameters().addAll(parameters);
        return this;
    }

    public ImportRequestAdapter organization(String organizationId) {
        importTask.setOrganization(organizationId);
        return this;
    }

    public ImportRequestAdapter brokenDependensies(BrokenDependenciesParameter parameter) {
        importTask.setBrokenDependencies(parameter.getValueName());
        return this;
    }

    public OperationResult<State> state() {
        return buildRequest(sessionStorage, State.class,
                new String[]{IMPORT_URI, taskId, STATE_URI}, new DefaultErrorHandler())
                .get();
    }

    public OperationResult<ImportTask> getTask() {
        return buildTaskRequest()
                .get();
    }

    protected JerseyRequest<ImportTask> buildTaskRequest() {
        return buildRequest(sessionStorage, ImportTask.class,
                new String[]{IMPORT_URI, taskId}, new DefaultErrorHandler());
    }

    public OperationResult<ImportTask> restartTask(ImportTask task) {
        return buildTaskRequest()
                .put(task);
    }

    public OperationResult<ImportTask> cancelTask() {
        return buildTaskRequest()
                .delete();
    }

    public <R> RequestExecution asyncState(final Callback<OperationResult<State>, R> callback) {
        final JerseyRequest<State> request = buildRequest(sessionStorage, State.class, new String[]{IMPORT_URI, taskId, STATE_URI});
        RequestExecution task = new RequestExecution(new Runnable() {
            @Override
            public void run() {
                callback.execute(request.get());
            }
        });
        ThreadPoolUtil.runAsynchronously(task);
        return task;
    }
}
