package com.example.lab3

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val progress: Float = 0f
)

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectById(id: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProject(project: ProjectEntity)

    @Update
    fun updateProject(project: ProjectEntity)

    @Delete
    fun deleteProject(project: ProjectEntity)
}
@Database(entities = [ProjectEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "projects_database")
                    .build().also { Instance = it }
            }
        }
    }
}

class ProjectRepository(private val projectDao: ProjectDao) {
    val projects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProjectById(id: String) = projectDao.getProjectById(id)

    suspend fun addProject(project: ProjectEntity) {
        withContext(Dispatchers.IO) { projectDao.insertProject(project) }
    }

    suspend fun updateProject(project: ProjectEntity) {
        withContext(Dispatchers.IO) { projectDao.updateProject(project) }
    }

    suspend fun deleteProject(project: ProjectEntity) {
        withContext(Dispatchers.IO) { projectDao.deleteProject(project) }
    }
}