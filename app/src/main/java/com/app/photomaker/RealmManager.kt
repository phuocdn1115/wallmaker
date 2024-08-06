package com.app.photomaker

import android.content.Context
import io.realm.*

class RealmManager(context: Context) {
    private var realm: Realm

    init {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
            .schemaVersion(1)
            .migration(MyMigration())
            .build()
        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
    }

    fun clear() {
        realm = Realm.getDefaultInstance()
        val configs = realm.configuration
        realm.close()
        Realm.deleteRealm(configs)
    }

    fun <T : RealmObject?> getMaxId(type: Class<T>, paramId: String): Number? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).max(paramId)
    }

    fun <T : RealmObject?> clear(type: Class<T>) {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.delete(type)
        realm.commitTransaction()
        realm.close()
    }

    fun <T : RealmObject?> delete(item: T) {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        item?.deleteFromRealm()
        realm.commitTransaction()
        realm.close()
    }

    fun <T : RealmObject?> save(item: T) {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(item)
        realm.commitTransaction()
        realm.close()
    }

    fun <T : RealmObject?> findFirst(type: Class<T>, key: String, value: Int): T? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).findFirst()
    }

    fun <T : RealmObject?> findFirst(type: Class<T>, key: String, value: String?): T? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).findFirst()
    }

    fun <T : RealmObject?> findFirst(type: Class<T>, key: String, value: Int?): T? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).findFirst()
    }

    fun <T : RealmObject?> findFirst(type: Class<T>): T? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).findFirst()
    }

    fun <T : RealmObject?> findAll(type: Class<T>): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).findAll()
    }

    fun <T : RealmObject?> findAllSorted(
        type: Class<T>,
        field: String,
        sort: Sort
    ): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).findAll().sort(field, sort)
    }

    fun <T : RealmObject?> findAllLimitSorted(
        type: Class<T>,
        field: String,
        sort: Sort,
        limit: Long = 40
    ): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).limit(limit).findAll().sort(field, sort)
    }

    fun <T : RealmObject?> findAllAsync(type: Class<T>): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).findAllAsync()
    }

    fun <T : RealmObject?> findAllLimitAsync(type: Class<T>, limit: Long = 3): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).limit(limit).findAllAsync()
    }

    fun <T : RealmObject?> findAllSortedAsync(
        type: Class<T>,
        field: String,
        sort: Sort,
        limit: Long = 50
    ): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).sort(field, sort).limit(limit).findAllAsync()
    }

    fun <T : RealmObject?> findAllSortedAsync(
        type: Class<T>,
        key: String,
        value: Boolean,
        field: String,
        sort: Sort
    ): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).findAllAsync().sort(field, sort)
    }

    fun <T : RealmObject?> findAllSortedLimitAsync(
        type: Class<T>,
        key: String,
        value: Boolean,
        field: String,
        sort: Sort,
        limit: Long = 50
    ): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).sort(field, sort).limit(limit).findAll()
    }

    fun <T : RealmObject?> findAllEqualSortedLimitAsync(
        type: Class<T>,
        key: String,
        value: Boolean,
        key2: String,
        value2: Boolean,
        field: String,
        sort: Sort,
        limit: Long = 50
    ): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).equalTo(key2,value2).sort(field, sort).limit(limit).findAll()
    }

    fun <T : RealmObject?> findOneAsync(type: Class<T>, key: String, value: Int): T {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).findFirstAsync()
    }

    fun <T : RealmObject?> findAllSortedAsync(
        type: Class<T>,
        key: String,
        value: Int,
        field: String,
        sort: Sort
    ): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).findAllAsync().sort(field, sort)
    }

    fun <T : RealmObject?> findAllSortedAsync(
        type: Class<T>,
        key: String,
        value: String
    ): RealmResults<*>? {
        realm = Realm.getDefaultInstance()
        return realm.where(type).contains(key, value, Case.INSENSITIVE).findAllAsync()
            .sort("name", Sort.ASCENDING)
    }

    fun <T : RealmObject?> findAllSortedAsync(
        type: Class<T>,
        key: String,
        value: Int
    ): RealmResults<*> {
        realm = Realm.getDefaultInstance()
        return realm.where(type).equalTo(key, value).findAllAsync().sort("name", Sort.ASCENDING)
    }

    fun <T : RealmObject?> findAllSortedAsync(
        type: Class<T>,
        key1: String,
        value1: String,
        key2: String,
        value2: Boolean
    ): RealmResults<*>? {
        return realm.where(type).equalTo(key2, value2).contains(key1, value1, Case.INSENSITIVE)
            .findAllAsync().sort("name", Sort.ASCENDING)
    }

    fun <T : RealmObject?> findFirst1(type: Class<T>, key: String, value: String): T? {
        return realm.where(type).contains(key, value, Case.INSENSITIVE).findFirst()
    }

    private class MyMigration : RealmMigration {
        override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {

        }
    }
}