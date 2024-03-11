import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

fun main() {
  val tmuxPanes = getTmuxPanes()
  val activeTmuxPane = getActiveTmuxPane()
  val widestTmuxPane = tmuxPanes?.maxBy { it.width }

  "Tmux Panes: $tmuxPanes".println()
  "Active Pane: $activeTmuxPane".println()
  "Widest Pane: $widestTmuxPane".println()

  if(widestTmuxPane != null && activeTmuxPane != widestTmuxPane.paneId && tmuxPanes.size > 1) {
    "Swapping ${widestTmuxPane.paneId} and $activeTmuxPane".println()
    tmuxSwitchPane(widestTmuxPane.paneId, activeTmuxPane)
  }
}

@OptIn(ExperimentalForeignApi::class)
fun executeCommand(
  command: String,
  trim: Boolean = true,
  redirectStderr: Boolean = true,
): String {
  val commandToExecute = if (redirectStderr) "$command 2>&1" else command
  val fp = popen(commandToExecute, "r") ?: error("Failed to run command: $command")

  val stdout = buildString {
    val buffer = ByteArray(4096)
    while (true) {
      val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
      append(input.toKString())
    }
  }

  val status = pclose(fp)
  if (status != 0) {
    error("Command `$command` failed with status $status${if (redirectStderr) ": $stdout" else ""}")
  }

  return if (trim) stdout.trim() else stdout
}

fun Any.println() = println(this)


data class TmuxPane(
  val paneId: String,
  val width: Int
)

fun getTmuxPanes(): List<TmuxPane>? {
  return executeCommand(
    "tmux list-panes -F '#{pane_index}:#{pane_width}'"
  ).let {
    it.split("\n").map { paneLine ->
      with(paneLine.split(":")) {
        TmuxPane(get(0), get(1).toInt())
      }
    }
  }
}

fun getActiveTmuxPane(): String {
  return executeCommand(
//    "tmux display-message -p '#I'"
    "tmux display -p '#{pane_index}'"
  )
}

fun tmuxSwitchPane(
  paneIdOne: String,
  paneIdTwo: String,
): String? {
  return executeCommand(
    "tmux swap-pane -s${paneIdOne} -t${paneIdTwo}"
  )
}
