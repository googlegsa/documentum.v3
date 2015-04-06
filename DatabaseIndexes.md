# Introduction #

Connector performance can be dramatically improved by adding database indexes. This page describes the types of indexes that can improve performance, and how to decide what to index.

We recommend analyzing the connector query performance, and then iteratively adding an index and remeasuring performance until query performance is acceptable. Usually the dm\_sysobject queries are fast enough, and the dm\_audittrail queries are more likely to be slow.

In general, the index over the fewest attributes that solves the problem is preferred, because indexes over multiple columns are slower for insertions. For the best overall system performance, you should choose the attribute(s) with the highest specificity and limit the index to those. Highest specificity here means the attribute whose use in the query restricts the results the most.

# Queries #

The dm\_sysobject query looks like this:

```
select i_chronicle_id, r_object_id, r_modify_date 
from dm_sysobject 
where (r_object_type='dm_document') 
    and ((r_modify_date = date('2010-01-06 07:02:36','yyyy-mm-dd hh:mi:ss') 
            and r_object_id > '09...') 
        OR (r_modify_date > date('2010-01-06 07:02:36','yyyy-mm-dd hh:mi:ss'))) 
order by r_modify_date,r_object_id 
ENABLE (return_top 100)
```

The date, object ID, and return\_top values will vary at runtime. The root object type (dm\_sysobject here) and included object types (dm\_document here) will vary depending on your connector configuration.

The dm\_audittrail query looks like this:

```
select r_object_id, chronicle_id, audited_obj_id, time_stamp_utc
from dm_audittrail
where (event_name='dm_destroy' or event_name='dm_prune')
    and ((time_stamp_utc = date('2009-12-16 10:09:00',''yyyy-mm-dd hh:mi:ss'') 
            and (r_object_id > '5f...')) 
        OR (time_stamp_utc > date('2009-12-16 10:09:00',''yyyy-mm-dd hh:mi:ss'')))
order by time_stamp_utc,r_object_id
ENABLE (return_top 100)
```

The date and return\_top values will vary at runtime, but the event\_name values are constant.

# Details #

For the dm\_audittrail, it depends on the age of entries that are kept in the audit trail, and the types of events that are audited. The time\_stamp\_utc attribute is indexed by default, but the event\_name attribute is not. In small scale experiments, an index on either of them improves performance by at least a factor of ten. If you have an old docbase with a very large audit trail, the existing index on time\_stamp\_utc might be enough, but if you have a more recent, archival docbase with lots of activity but relatively few deletions, you might try adding an index for event\_name.
```
EXECUTE make_index WITH type_name='dm_audittrail', attribute='event_name';
```

If query analysis shows that the dm\_sysobject query needs to run faster, the attribute with the highest specificity is almost certainly r\_modify\_date:
```
EXECUTE make_index WITH type_name='dm_sysobject', attribute='r_modify_date';
```

# Optimal Indexes #

**Note:** These indexes shown in this section may slow down other Documentum applications unnecessarily. They are given here for completeness, in case they are useful in some difficult database optimizations. These indexes provided the best performance and lowest optimizer costs during testing, but in most cases a smaller index will provide almost the same practical benefits. For example, the i\_has\_folder, i\_is\_deleted, and r\_object\_type attributes of dm\_sysobject are generally of low specificity, and the index shown here has a lower optimizer cost, but did not provide a measurable improvement in query performance over the simpler index shown above.

In general, the order of the attributes in the make\_index function matters, and should match their appearance in the SQL queries.

```
EXECUTE make_index WITH type_name='dm_sysobject', attribute='i_has_folder',
    attribute='i_is_deleted', attribute='r_object_type', attribute='r_modify_date';

EXECUTE make_index WITH type_name='dm_audittrail', attribute='event_name', attribute='time_stamp_utc';
```

# Additional WHERE Clause #

If you add conditions to the WHERE clause of the dm\_sysobject query, or modify the root object type, then you may need to alter your indexes as well. For example, you might introduce a highly restrictive condition on an attribute not listed here. If you edit the **Advanced Configuration** section of the connector configuration page in the Admin Console, you may need to reanalyze your database queries.

# Older Connector Versions #

In version 2.0 or earlier, the dm\_audittrail.time\_stamp attribute was used in place of time\_stamp\_utc. The time\_stamp attribute is not indexed by default. Starting with version 2.0.2, the time\_stamp\_utc attribute is used instead, for better performance.