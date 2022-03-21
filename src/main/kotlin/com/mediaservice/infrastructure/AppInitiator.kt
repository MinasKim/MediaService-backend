package com.mediaservice.infrastructure

import com.mediaservice.domain.ActorTable
import com.mediaservice.domain.CreatorTable
import com.mediaservice.domain.GenreTable
import com.mediaservice.domain.LikeTable
import com.mediaservice.domain.MediaContentsActorTable
import com.mediaservice.domain.MediaContentsCreatorTable
import com.mediaservice.domain.MediaContentsGenreTable
import com.mediaservice.domain.MediaContentsTable
import com.mediaservice.domain.MediaSeriesTable
import com.mediaservice.domain.MediaTable
import com.mediaservice.domain.ProfileTable
import com.mediaservice.domain.Role
import com.mediaservice.domain.UserTable
import com.mediaservice.domain.WishContentTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class AppInitiator {

    companion object {
        fun localInit() {
            transaction {
                SchemaUtils.drop(
                    UserTable, ProfileTable, MediaTable, MediaSeriesTable, MediaContentsTable,
                    ActorTable, CreatorTable, GenreTable, MediaContentsActorTable, MediaContentsGenreTable,
                    MediaContentsTable, LikeTable, WishContentTable
                )
                SchemaUtils.create(
                    UserTable, ProfileTable, MediaTable, MediaSeriesTable, MediaContentsTable,
                    ActorTable, CreatorTable, GenreTable, MediaContentsActorTable, MediaContentsGenreTable,
                    MediaContentsCreatorTable, LikeTable, WishContentTable

                )

                UserTable.insert {
                    it[email] = "admin@cotlin.com"
                    it[password] = "admin1pw!@"
                    it[role] = Role.ADMIN
                }

                val userIds = ArrayList<UUID>()
                for (i in 1..5) {
                    userIds.add(
                        UserTable.insertAndGetId {
                            it[email] = "user$i@cotlin.com"
                            it[password] = "user${i}pw!@"
                            it[role] = Role.USER
                        }.value
                    )
                }

                val profileIds = ArrayList<UUID>()
                for (i in userIds) {
                    for (j in 1..3) {
                        profileIds.add(
                            ProfileTable.insertAndGetId {
                                it[user_id] = i
                                it[name] = "프로필 $j"
                                it[rate] = "19+"
                                it[mainImage] = "프로필 ${j}의 메인 이미지"
                                it[isDeleted] = false
                            }.value
                        )
                    }
                }

                val mediaContentsIds = ArrayList<UUID>()
                for (i in 1..15) {
                    mediaContentsIds.add(
                        MediaContentsTable.insertAndGetId {
                            it[title] = "전체 미디어 ${i}의 타이틀"
                            it[synopsis] = "전체 미디어 ${i}의 시놉시스"
                            it[trailer] = "전체 미디어 ${i}의 트레일러"
                            it[thumbnail] = "전체 미디어 ${i}의 썸네일"
                            it[rate] = "19+"
                            it[isSeries] = true
                            it[isDeleted] = false
                        }.value
                    )
                }

                val mediaSeriesIds = ArrayList<UUID>()
                for (i in 1..15) {
                    for (j in 1..2) {
                        mediaSeriesIds.add(
                            MediaSeriesTable.insertAndGetId {
                                it[title] = "전체 미디어 ${i}의 시즌 $j"
                                it[order] = j
                                it[isDeleted] = false
                                it[mediaContents] = mediaContentsIds[i - 1]
                            }.value
                        )
                    }
                }

                for (i in 1..15) {
                    for (j in 1..2) {
                        for (k in 1..8) {
                            MediaTable.insert {
                                it[name] = "미디어 $i 시즌 $j ${k}화"
                                it[synopsis] = "미디어 $i 시즌 $j ${k}화의 시놉"
                                it[order] = k
                                it[url] = "dummyurl$i/$j/$k.mp4"
                                it[thumbnail] = "미디어 $i 시즌 $j ${k}화의 썸네일"
                                it[runningTime] = 60
                                it[isDeleted] = false
                                it[mediaSeries] = mediaSeriesIds[((i - 1) * 2 + j) - 1]
                            }
                        }
                    }
                }

                val genreData = listOf(
                    "로맨스", "판타지", "스릴러", "코미디", "SF",
                    "공포", "미스터리", "애니메이션"
                )

                val genreIds = ArrayList<UUID>()
                for (i in genreData) {
                    genreIds.add(
                        GenreTable.insertAndGetId {
                            it[name] = i
                            it[isDeleted] = false
                        }.value
                    )
                }

                val actorData = listOf(
                    "고광표", "김민하", "김영민", "배진우", "우창혁",
                    "유동필", "이민수"
                )

                val actorIds = ArrayList<UUID>()
                for (i in actorData) {
                    actorIds.add(
                        ActorTable.insertAndGetId {
                            it[name] = i
                            it[isDeleted] = false
                        }.value
                    )
                }

                val creatorData = listOf(
                    "봉준호", "스필버그", "박찬욱", "홍상수", "이병헌"
                )

                val creatorIds = ArrayList<UUID>()
                for (i in creatorData) {
                    creatorIds.add(
                        CreatorTable.insertAndGetId {
                            it[name] = i
                            it[isDeleted] = false
                        }.value
                    )
                }

                for ((i, v) in mediaContentsIds.withIndex()) {
                    val actorList = List(3) {
                        actorIds.random()
                    }.toSet().toList()

                    for (j in actorList) {
                        MediaContentsActorTable.insert {
                            it[mediaContents] = v
                            it[actor] = j
                        }
                    }

                    val genreList = List(2) {
                        genreIds.random()
                    }.toSet().toList()

                    for (j in genreList) {
                        MediaContentsGenreTable.insert {
                            it[mediaContents] = v
                            it[genre] = j
                        }
                    }

                    MediaContentsCreatorTable.insert {
                        it[mediaContents] = v
                        it[creator] = creatorIds[i % creatorIds.size]
                    }
                }
            }
        }
    }
}
