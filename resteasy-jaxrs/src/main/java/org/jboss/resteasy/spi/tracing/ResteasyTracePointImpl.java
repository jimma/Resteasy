package org.jboss.resteasy.spi.tracing;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ResteasyTracePointImpl extends HashMap implements ResteasyTracePoint
{
   protected long start;

   protected long duration;

   protected long finish;

   protected ResteasyTracePoint parent;

   protected String operationName;

   protected List<ResteasyTracePoint> childs = new LinkedList<ResteasyTracePoint>();

   public ResteasyTracePointImpl(ResteasyTracePoint parent, String operationName)
   {
      this.parent = parent;
      this.operationName = operationName;

   }

   public ResteasyTracePointImpl(String operationName)
   {
      this.operationName = operationName;

   }

   @Override
   public void close() throws IOException
   {
      finish();

   }

   @Override
   public ResteasyTracePoint start()
   {
      start = System.currentTimeMillis();
      if (parent != null)
      {
         parent.getChildren().add(this);
      }
      return this;
   }

   @Override
   public ResteasyTracePoint finish()
   {
     
      this.finish = System.currentTimeMillis();
      this.duration = finish -start;
      return this;
   }

   @Override
   public String getName()
   {
      return this.operationName;
   }

   @Override
   public long getDuration()
   {
      return this.duration;
   }


   public List<ResteasyTracePoint> getChildren()
   {
      return this.childs;
   }
   
   @Override
   public String toString()
   {
      return this.operationName + " : start= " + start + " finish=" + finish + " Duration = " + duration;
   }

   @Override
   public String getContextString()
   {
      return  this.operationName;
   }
}
