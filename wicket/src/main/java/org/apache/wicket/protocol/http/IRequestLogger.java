/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.protocol.http;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.IClusterable;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.protocol.http.RequestLogger.ISessionLogInfo;
import org.apache.wicket.protocol.http.RequestLogger.SessionData;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.string.AppendingStringBuffer;


/**
 * Interface for the request logger and viewer.
 * 
 * @see org.apache.wicket.Application#newRequestLogger()
 * 
 * @author jcompagner
 */
public interface IRequestLogger
{

	/**
	 * @return The total created sessions counter
	 */
	public abstract int getTotalCreatedSessions();

	/**
	 * @return The peak sessions counter
	 */
	public abstract int getPeakSessions();

	/**
	 * This method returns a List of the current requests that are in mem. This is a readonly list.
	 * 
	 * @return Collection of the current requests
	 */
	public abstract List<RequestData> getRequests();


	/**
	 * @return Collection of live Sessions Data
	 */
	public SessionData[] getLiveSessions();

	/**
	 * @return The current active requests
	 */
	public int getCurrentActiveRequestCount();

	/**
	 * @return The peak active requests
	 */
	public int getPeakActiveRequestCount();

	/**
	 * called when the session is created and has an id. (for http it means that the http session is
	 * created)
	 * 
	 * @param id
	 *            the session id
	 */
	public abstract void sessionCreated(String id);

	/**
	 * Method used to cleanup a livesession when the session was invalidated by the webcontainer
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public abstract void sessionDestroyed(String sessionId);

	/**
	 * This method is called when the request is over. This will set the total time a request takes
	 * and cleans up the current request data.
	 * 
	 * @param timeTaken
	 *            the time taken in milliseconds
	 */
	public abstract void requestTime(long timeTaken);

	/**
	 * Called to monitor removals of objects out of the {@link ISessionStore}
	 * 
	 * @param value
	 *            the object being removed
	 */
	public abstract void objectRemoved(Object value);

	/**
	 * Called to monitor updates of objects in the {@link ISessionStore}
	 * 
	 * @param value
	 *            the object being updated
	 */
	public abstract void objectUpdated(Object value);

	/**
	 * Called to monitor additions of objects in the {@link ISessionStore}
	 * 
	 * @param value
	 *            the object being created/added
	 */
	public abstract void objectCreated(Object value);

	/**
	 * Sets the target that was the response target for the current request
	 * 
	 * @param target
	 *            the response target
	 */
	public abstract void logResponseTarget(IRequestTarget target);

	/**
	 * Sets the target that was the event target for the current request
	 * 
	 * @param target
	 *            the event target
	 */
	public abstract void logEventTarget(IRequestTarget target);

	/**
	 * This class hold the information one request of a session has.
	 * 
	 * @author jcompagner
	 */
	public static class RequestData implements IClusterable
	{
		private static final long serialVersionUID = 1L;

		private long startDate;
		private long timeTaken;
		private final List<String> entries = new ArrayList<String>(5);
		private String eventTarget;
		private String responseTarget;

		private String sessionId;

		private long totalSessionSize;

		private Object sessionInfo;

		private int activeRequest;

		/**
		 * @return The time taken for this request
		 */
		public Long getTimeTaken()
		{
			return new Long(timeTaken);
		}

		/**
		 * @param activeRequest
		 *            The number of active request when this request happened
		 */
		public void setActiveRequest(int activeRequest)
		{
			this.activeRequest = activeRequest;
		}

		/**
		 * @return The number of active request when this request happened
		 */
		public int getActiveRequest()
		{
			return activeRequest;
		}

		/**
		 * @return The session object info, created by {@link ISessionLogInfo#getSessionInfo()}
		 */
		public Object getSessionInfo()
		{
			return sessionInfo;
		}

		/**
		 * Set the session info object of the session for this request.
		 * 
		 * @param sessionInfo
		 */
		public void setSessionInfo(Object sessionInfo)
		{
			this.sessionInfo = sessionInfo;
		}

		/**
		 * @param sizeInBytes
		 */
		public void setSessionSize(long sizeInBytes)
		{
			totalSessionSize = sizeInBytes;
		}

		/**
		 * @param id
		 */
		public void setSessionId(String id)
		{
			sessionId = id;
		}

		/**
		 * @return The time taken for this request
		 */
		public Date getStartDate()
		{
			return new Date(startDate);
		}

		/**
		 * @return The event target string
		 */
		public String getEventTarget()
		{
			return eventTarget;
		}

		/**
		 * @return The response target string
		 */
		public String getResponseTarget()
		{
			return responseTarget;
		}

		/**
		 * @param target
		 */
		public void addResponseTarget(String target)
		{
			responseTarget = target;
		}

		/**
		 * @param target
		 */
		public void addEventTarget(String target)
		{
			eventTarget = target;
		}

		/**
		 * @param timeTaken
		 */
		public void setTimeTaken(long timeTaken)
		{
			this.timeTaken = timeTaken;
			startDate = System.currentTimeMillis() - timeTaken;
		}

		/**
		 * @param string
		 */
		public void addEntry(String string)
		{
			entries.add(string);
		}

		/**
		 * @return All entries of the objects that are created/updated or removed in this request
		 */
		public String getAlteredObjects()
		{
			AppendingStringBuffer sb = new AppendingStringBuffer();
			for (int i = 0; i < entries.size(); i++)
			{
				String element = entries.get(i);
				sb.append(element);
				if (entries.size() != i + 1)
				{
					sb.append("<br/>");
				}
			}
			return sb.toString();
		}

		/**
		 * @return The session id for this request
		 */
		public String getSessionId()
		{
			return sessionId;
		}

		/**
		 * @return The total session size.
		 */
		public Long getSessionSize()
		{
			return new Long(totalSessionSize);
		}

		@Override
		public String toString()
		{
			return "Request[timetaken=" + getTimeTaken() + ",sessioninfo=" + sessionInfo +
				",sessionid=" + sessionId + ",sessionsize=" + totalSessionSize + ",request=" +
				eventTarget + ",response=" + responseTarget + ",alteredobjects=" +
				getAlteredObjects() + ",activerequest=" + activeRequest + "]";
		}
	}
}