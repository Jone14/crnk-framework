package io.crnk.jpa.integration;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryV2;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.jpa.AbstractJpaJerseyTest;
import io.crnk.jpa.model.BasicAttributesTestEntity;
import io.crnk.jpa.model.JpaTransientTestEntity;
import io.crnk.jpa.model.TestEnum;
import org.junit.Assert;
import org.junit.Test;

public class BasicAttributesEndToEndTest extends AbstractJpaJerseyTest {

	@Test
	public void testCanStoreBasicAttributeValues() {
		ResourceRepositoryV2<BasicAttributesTestEntity, Serializable> repo =
				client.getRepositoryForType(BasicAttributesTestEntity.class);

		BasicAttributesTestEntity test = new BasicAttributesTestEntity();
		test.setId(1L);
		test.setLongValue(15);
		test.setNullableBooleanValue(false);
		test.setBooleanValue(true);
		test.setLongValue(13L);
		test.setNullableLongValue(14L);
		repo.create(test);

		ResourceList<BasicAttributesTestEntity> list = repo.findAll(new QuerySpec(BasicAttributesTestEntity.class));
		Assert.assertEquals(1, list.size());
		BasicAttributesTestEntity saved = list.get(0);
		Assert.assertEquals(saved.getLongValue(), test.getLongValue());
		Assert.assertEquals(saved.getNullableBooleanValue(), test.getNullableBooleanValue());
		Assert.assertEquals(saved.getBooleanValue(), test.getBooleanValue());
		Assert.assertEquals(saved.getNullableBooleanValue(), test.getNullableBooleanValue());
	}

	@Test
	public void testJpaTransientFieldIgnored() {
		QuerySpec querySpec = new QuerySpec(JpaTransientTestEntity.class);

		ResourceRepositoryV2<JpaTransientTestEntity, Serializable> repo =
				client.getRepositoryForType(JpaTransientTestEntity.class);

		JpaTransientTestEntity entity = new JpaTransientTestEntity();
		entity.setId(12L);
		repo.create(entity);

		List<JpaTransientTestEntity> list = repo.findAll(querySpec);
		Assert.assertEquals(1, list.size());
		entity = list.get(0);
		Assert.assertNotNull(entity);

		repo.delete(entity.getId());
		list = repo.findAll(querySpec);
		Assert.assertEquals(0, list.size());
	}


	@Test
	public void testExactLikeFilterByEnum() {
		ResourceRepositoryV2<BasicAttributesTestEntity, Serializable> repo =
				client.getRepositoryForType(BasicAttributesTestEntity.class);

		BasicAttributesTestEntity test1 = new BasicAttributesTestEntity();
		test1.setId(1L);
		test1.setEnumValue(TestEnum.BAR);
		repo.create(test1);

		BasicAttributesTestEntity test2 = new BasicAttributesTestEntity();
		test2.setId(2L);
		test2.setEnumValue(TestEnum.FOO);
		repo.create(test2);

		QuerySpec querySpec = new QuerySpec(BasicAttributesTestEntity.class);
		querySpec.addFilter(new FilterSpec(Arrays.asList(BasicAttributesTestEntity.ATTR_enumValue), FilterOperator.LIKE, "FOO"));
		List<BasicAttributesTestEntity> list = repo.findAll(querySpec);
		Assert.assertEquals(1, list.size());
		BasicAttributesTestEntity testEntity = list.get(0);
		Assert.assertEquals(2L, testEntity.getId().longValue());
	}

	@Test
	public void testWildcardLikeFilterByEnum() {
		ResourceRepositoryV2<BasicAttributesTestEntity, Serializable> repo =
				client.getRepositoryForType(BasicAttributesTestEntity.class);

		BasicAttributesTestEntity test1 = new BasicAttributesTestEntity();
		test1.setId(1L);
		test1.setEnumValue(TestEnum.BAR);
		repo.create(test1);

		BasicAttributesTestEntity test2 = new BasicAttributesTestEntity();
		test2.setId(2L);
		test2.setEnumValue(TestEnum.FOO);
		repo.create(test2);

		QuerySpec querySpec = new QuerySpec(BasicAttributesTestEntity.class);
		querySpec.addFilter(new FilterSpec(Arrays.asList(BasicAttributesTestEntity.ATTR_enumValue), FilterOperator.LIKE, "BA%"));
		List<BasicAttributesTestEntity> list = repo.findAll(querySpec);
		Assert.assertEquals(1, list.size());
		BasicAttributesTestEntity testEntity = list.get(0);
		Assert.assertEquals(1L, testEntity.getId().longValue());
	}


	@Test
	public void testNullFilterByEnum() {
		ResourceRepositoryV2<BasicAttributesTestEntity, Serializable> repo =
				client.getRepositoryForType(BasicAttributesTestEntity.class);

		BasicAttributesTestEntity test1 = new BasicAttributesTestEntity();
		test1.setId(1L);
		test1.setEnumValue(null);
		repo.create(test1);

		BasicAttributesTestEntity test2 = new BasicAttributesTestEntity();
		test2.setId(2L);
		test2.setEnumValue(TestEnum.FOO);
		repo.create(test2);

		QuerySpec querySpec = new QuerySpec(BasicAttributesTestEntity.class);
		querySpec.addFilter(new FilterSpec(Arrays.asList(BasicAttributesTestEntity.ATTR_enumValue), FilterOperator.EQ, null));
		List<BasicAttributesTestEntity> list = repo.findAll(querySpec);
		Assert.assertEquals(1, list.size());
		BasicAttributesTestEntity testEntity = list.get(0);
		Assert.assertEquals(1L, testEntity.getId().longValue());
	}

	@Test
	public void testFilterByOffsetDateTime() {
		ResourceRepositoryV2<BasicAttributesTestEntity, Serializable> repo =
				client.getRepositoryForType(BasicAttributesTestEntity.class);

		OffsetDateTime now = OffsetDateTime.now();

		BasicAttributesTestEntity test1 = new BasicAttributesTestEntity();
		test1.setId(1L);
		test1.setOffsetDateTimeValue(now);
		repo.create(test1);

		BasicAttributesTestEntity test2 = new BasicAttributesTestEntity();
		test2.setId(2L);
		test2.setOffsetDateTimeValue(now.plusHours(13));
		repo.create(test2);

		QuerySpec querySpec = new QuerySpec(BasicAttributesTestEntity.class);
		querySpec.addFilter(new FilterSpec(Arrays.asList(BasicAttributesTestEntity.ATTR_offsetDateTimeValue), FilterOperator.EQ, now));
		List<BasicAttributesTestEntity> list = repo.findAll(querySpec);
		Assert.assertEquals(1, list.size());
		BasicAttributesTestEntity testEntity = list.get(0);
		Assert.assertEquals(1L, testEntity.getId().longValue());

		querySpec = new QuerySpec(BasicAttributesTestEntity.class);
		querySpec.addFilter(new FilterSpec(Arrays.asList(BasicAttributesTestEntity.ATTR_offsetDateTimeValue), FilterOperator.GT, now));
		list = repo.findAll(querySpec);
		Assert.assertEquals(1, list.size());
		testEntity = list.get(0);
		Assert.assertEquals(2L, testEntity.getId().longValue());
	}
}
